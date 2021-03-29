package com.antzuhl.chat;

import com.antzuhl.chat.store.MappedFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MappedFileTest {

    @Test
    public void mappedFileCreateTest() throws IOException {
        MappedFile mappedFile = new MappedFile("d:/mmap-test/000000000", 1024 * 1024 * 10);
        assert mappedFile.getWrotePosition() == 0;
        boolean success = mappedFile.appendMessage("mmaptest".getBytes());
        System.out.println(success);
    }

}
