package com.company.statistics;

public class Statistics {

    private static IHttpStatisticsWriter writer;

    private Statistics() {
    }

    public static IHttpStatisticsWriter getInstance() {

        if (writer == null) {
            writer = new HttpStatisticsWriterXml();
        }
        return writer;

    }

}
