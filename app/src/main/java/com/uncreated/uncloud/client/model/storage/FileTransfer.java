package com.uncreated.uncloud.client.model.storage;

public class FileTransfer {
    public static final int PART_SIZE = 1048576;//1mb

    private String path;
    private int part;
    private int parts;
    private byte[] data;

    public FileTransfer(String path, int part, int size) {
        this.path = path;
        this.part = part;
        this.parts = size / PART_SIZE;
        if (size % PART_SIZE != 0) {
            parts++;
        }
        this.data = new byte[size];
    }

    public String getPath() {
        return path;
    }

    public int getPart() {
        return part;
    }

    public byte[] getData() {
        return data;
    }

    public int getParts() {
        return parts;
    }

    public static int getSizeOfPart(long fileSize, int part) {
        fileSize -= part * PART_SIZE;
        if (fileSize > PART_SIZE) {
            fileSize = PART_SIZE;
        }

        return (int) fileSize;
    }

    public static long getShift(int part) {
        return PART_SIZE * part;
    }
}
