package com.company.settings;

public class Settings {

    private static IReaderSettings reader;

    private Settings() {
    }

    public static IReaderSettings getInstance() {

        if (reader == null) {
            reader = new ReaderSettingsXml();
        }
        return reader;

    }

}
