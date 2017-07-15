package com.company.logs;

import java.io.*;

public abstract class AHttpLogWriterTxt implements IHttpLogWriter {

    private RandomAccessFile accessFile;

    public AHttpLogWriterTxt() {

        try {
            this.accessFile = new RandomAccessFile(IHttpLogWriter.getNewLogFileByDefault("logs"), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public AHttpLogWriterTxt(File LogFile) {

        try {
            this.accessFile = new RandomAccessFile(LogFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void add(String text) {

        try {
            accessFile.write(text.concat("\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void add(String text, boolean displayOnScreen) {

        if (displayOnScreen) {
            System.out.println(text);
        }

        add(text);

    }

}
