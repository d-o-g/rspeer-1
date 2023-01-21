package org.rspeer.injector;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public final class Buffer implements AutoCloseable {

    private final DataInputStream stream;

    public Buffer(byte[] payload) {
        stream = new DataInputStream(new ByteArrayInputStream(payload));
    }

    public long readLong() {
        try {
            return stream.readLong();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int readInt() {
        try {
            return stream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public short readShort() {
        try {
            return stream.readShort();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public byte readByte() {
        try {
            return stream.readByte();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean readBoolean() {
        try {
            return stream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String readUTF() {
        try {
            return stream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assertValidMagic(int header) {
        if (readInt() != header) {
            throw new IllegalStateException("Incompatible modscript! Please update bot");
        }
    }
}