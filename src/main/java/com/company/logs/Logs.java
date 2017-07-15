package com.company.logs;

public class Logs {

    private static IHttpLogWriter writer;

    private Logs() {}

    public static IHttpLogWriter getInstance() {

        if (writer == null) {
            writer = new HttpLogWriterTxt();
        }

        return writer;

    }

}
