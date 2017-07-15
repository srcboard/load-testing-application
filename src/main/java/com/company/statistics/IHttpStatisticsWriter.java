package com.company.statistics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface IHttpStatisticsWriter {

    void addOne(Type key);

    int getCounter(Type key);

    String getInfo(Type key);

    void setValue(Type key, Integer value);

    void setValue(Type key, String value);

    void writeFile(String nameOfStatisticsFile);

    static File getNewStatisticsFileByDefault() {
        return getNewStatisticsFileByDefault("statistics");
    }

    static File getNewStatisticsFileByDefault(String nameOfStatisticsFile) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String absolutePath = new File(".").getAbsolutePath();

        File statisticsFolder = new File(absolutePath + "\\statistics");
        if (!statisticsFolder.exists()) {
            statisticsFolder.mkdir();
        }

        File logFile = new File(statisticsFolder.getAbsolutePath() + "\\" + String.format("%s %s.xml", nameOfStatisticsFile, dateFormat.format(new Date())));
        if (!statisticsFolder.exists()) {
            try {
                statisticsFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logFile;

    }

}
