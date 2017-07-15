package com.company.logs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface IHttpLogWriter {

    void add(String text);

    void add(String text, boolean displayOnScreen);

    static File getNewLogFileByDefault(String nameOfLogFile) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String absolutePath = new File(".").getAbsolutePath();

        File logFolder = new File(absolutePath + "\\logs");
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }

        File logFile = new File(logFolder.getAbsolutePath() + "\\" + String.format("%s %s.txt", nameOfLogFile, dateFormat.format(new Date())));
        if (!logFolder.exists()) {
            try {
                logFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logFile;

    }

}
