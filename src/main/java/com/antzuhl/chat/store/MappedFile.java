package com.antzuhl.chat.store;

import com.antzuhl.chat.common.AppendMessageCallback;
import com.antzuhl.chat.common.AppendMessageResult;
import com.antzuhl.chat.common.AppendMessageStatus;
import com.antzuhl.chat.message.MessageExt;
import com.antzuhl.chat.message.MessageExtBrokerInner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MappedFile extends ReferenceResource {

    private static final Logger log = LoggerFactory.getLogger(MappedFile.class);

    public static final int OS_PAGE_SIZE = 1024 * 4;

    private static final AtomicLong TOTAL_MAPPED_VIRTUAL_MEMORY = new AtomicLong(0);
    private static final AtomicInteger TOTAL_MAPPED_FILES = new AtomicInteger(0);
    /** current write position */
    protected final AtomicInteger wrotePosition = new AtomicInteger(0);
    /** current commit position */
    protected final AtomicInteger committedPosition = new AtomicInteger(0);
    /** current flush position */
    private final AtomicInteger flushedPosition = new AtomicInteger(0);
    /** file size */
    protected int fileSize;
    /** file channel */
    protected FileChannel fileChannel;
    /**
     * Message will put to here first, and then reput to FileChannel if writeBuffer is not null.
     */
    protected ByteBuffer writeBuffer = null;
    private String fileName;
    /** file start offset, and then use to file name */
    private long fileFromOffset;
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private volatile long storeTimestamp = 0;
    private boolean firstCreateInQueue = false;

    public MappedFile(final String fileName, final int size) throws IOException {
        init(fileName, size);
    }

    public void init(final String fileName, final int size) throws IOException {
        this.fileName = fileName;
        this.fileSize = size;
        this.file = new File(fileName);
        this.fileFromOffset = Long.parseLong(file.getName());

        ensureDirOK(this.file.getParent());
        /** check parent file */
        boolean ok = false;
        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(fileSize);
            TOTAL_MAPPED_FILES.incrementAndGet();
            ok = true;
        } catch (FileNotFoundException e) {
            log.error("#### MappedFile init {} file not fount, {}", this.fileName, e.getMessage());
        } catch (IOException e) {
            log.error("#### MappedFile {} failed, {}", this.fileName, e.getMessage());
        } finally {
            if (!ok && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }

    private static void ensureDirOK(final String dirName) {
        if (StringUtils.isNoneBlank(dirName)) {
            File file = new File(dirName);
            if (!file.exists()) {
                boolean result = file.mkdir();
                log.info("### MappedFile create dir:{}, result:{}", dirName, result);
            }
        }
    }

    public long getLastModifiedTimestamp() {
        return this.file.lastModified();
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public FileChannel getFileChannel() {
        return this.fileChannel;
    }

    public AppendMessageResult appendMessage(final MessageExtBrokerInner message, final AppendMessageCallback cb) {
        return appendMessagesInner(message, cb);
    }

    /** append message */
    public AppendMessageResult appendMessagesInner(final MessageExt ext, final AppendMessageCallback cb) {
        assert ext != null;
        assert cb != null;
        /** get current write position */
        int currentPos = wrotePosition.get();
        if (currentPos < this.fileSize) {
            ByteBuffer byteBuffer = this.writeBuffer != null
                    ? this.writeBuffer.slice() : this.mappedByteBuffer.slice();
            byteBuffer.position(currentPos);
            AppendMessageResult result;
            if (ext instanceof MessageExtBrokerInner) {
                result = cb.doAppend(this.fileFromOffset, byteBuffer, this.fileSize - currentPos, (MessageExtBrokerInner) ext);
            } else {
                result = new AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR);
            }
            this.wrotePosition.getAndAdd(result.getWroteBytes());
            this.storeTimestamp = result.getStoreTimestamp();
            return result;
        }
        log.warn("#### MappedFile appendMessagesInner happened unknown error.");
        return new AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR);
    }

    public long getFileFromOffset() {
        return this.fileFromOffset;
    }

    private boolean appendMessage(final byte[] data) {
        int currentPos = this.wrotePosition.get();
        if ((currentPos + data.length) <= this.fileSize) {
            try {
                this.fileChannel.position(currentPos);
                this.fileChannel.write(ByteBuffer.wrap(data));
            } catch (IOException e) {
                log.error("#### MappedFile error occurred when append message to mappedFile.", e);
            }
            this.wrotePosition.getAndAdd(data.length);
            return true;
        }
        return false;
    }

    private boolean appendMessage(final byte[] data, final int offset, final int length) {
        int currentPos = this.wrotePosition.get();
        if ((currentPos + length <= this.fileSize)) {
            try {
                this.fileChannel.position(currentPos);
                this.fileChannel.write(ByteBuffer.wrap(data, offset, length));
            } catch (IOException e) {
                log.error("#### MappedFile error occurred when append message to mappedFile.", e);
            }
            this.wrotePosition.getAndAdd(length);
            return true;
        }

        return false;
    }

    /**
     * @return 有效数据的最大位置
     */
    public int getReadPosition() {
        return this.writeBuffer == null ? this.wrotePosition.get() : this.committedPosition.get();
    }

    public boolean isFull() {
        return this.fileSize >= this.wrotePosition.get();
    }

    /** 是否可以刷入 */
    private boolean isAbleToFlush(final int flushLeastPages) {
        int flush = this.flushedPosition.get();
        int write = getReadPosition();

        if (this.isFull()) {
            return true;
        }
        if (flushLeastPages > 0) {
            return ((write / OS_PAGE_SIZE) - (flush / OS_PAGE_SIZE)) >= flushLeastPages;
        }
        return write > flush;
    }

    /** 是否可以提交 */
    protected boolean isAbleToCommit(final int commitLeastPages) {
        int flush = this.committedPosition.get();
        int write = this.wrotePosition.get();

        if (this.isFull()) {
            return true;
        }
        if (commitLeastPages > 0) {
            return ((write / OS_PAGE_SIZE) - (flush / OS_PAGE_SIZE)) >= commitLeastPages;
        }
        return write > flush;
    }

    private void commit0(MessageExt ext, int offset, int length) {

    }

    public int getFlushedPosition() {
        return flushedPosition.get();
    }

    public void setFlushedPosition(int pos) {
        this.flushedPosition.set(pos);
    }

    @Override
    public boolean cleanup(long currentRef) {
        return false;
    }
}
