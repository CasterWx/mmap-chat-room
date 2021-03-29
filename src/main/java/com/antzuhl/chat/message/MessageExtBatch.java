package com.antzuhl.chat.message;

import java.nio.ByteBuffer;

public class MessageExtBatch extends MessageExt {

    private static final long serialVersionUID = -2353110995348498537L;

    public ByteBuffer wrap() {
        assert getBody() != null;
        return ByteBuffer.wrap(getBody(), 0, getBody().length);
    }

    private ByteBuffer encodedBuff;

    public ByteBuffer getEncodedBuff() {
        return encodedBuff;
    }

    public void setEncodedBuff(ByteBuffer encodedBuff) {
        this.encodedBuff = encodedBuff;
    }
}
