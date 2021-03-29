package com.antzuhl.chat.common;

import com.antzuhl.chat.message.MessageExtBatch;
import com.antzuhl.chat.message.MessageExtBrokerInner;

import java.nio.ByteBuffer;

/**
 * Write messages callback interface
 */
public interface AppendMessageCallback {

    /**
     * After message serialization, write MapedByteBuffer
     *
     * @return How many bytes to write
     */
    AppendMessageResult doAppend(final long fileFromOffset, final ByteBuffer byteBuffer,
                                 final int maxBlank, final MessageExtBrokerInner msg);

}
