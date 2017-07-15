package com.company.settings;

import com.company.logs.Logs;
import com.company.logs.IHttpLogWriter;

import java.io.File;

public interface IReaderSettings {

    String getProperty(String property);

    static File findConfigurationFile(String NameOfConfigurationFile) {

        String absolutePath = new File(".").getAbsolutePath();

        File externalConfigurationFile = new File(absolutePath + ".\\" + NameOfConfigurationFile);
        if (externalConfigurationFile.exists()) {
            return externalConfigurationFile;
        }

        File InternalConfigurationFile = new File(absolutePath + "\\" + NameOfConfigurationFile);
        if (InternalConfigurationFile.exists()) {
            return InternalConfigurationFile;
        }

        File resourceConfigurationFile = new File(absolutePath + "\\src\\main\\resources\\" + NameOfConfigurationFile);
        if (resourceConfigurationFile.exists()) {
            return resourceConfigurationFile;
        }

        throw new RuntimeException();

    }

    static void writePropertiesFromFileToLog(IHttpLogWriter logWriter) {

        IHttpLogWriter log = null;
        if (logWriter == null) {
            log = Logs.getInstance();
        } else {
            log = logWriter;
        }

        log.add("url " + Settings.getInstance().getProperty("url"));
        log.add("numberRequestsPerSecond " + Settings.getInstance().getProperty("numberRequestsPerSecond"));
        log.add("testDurationInSeconds " + Settings.getInstance().getProperty("testDurationInSeconds"));
        log.add("logFolder " + Settings.getInstance().getProperty("logFolder"));
        log.add("outputFolder " + Settings.getInstance().getProperty("outputFolder"));

    }

}
