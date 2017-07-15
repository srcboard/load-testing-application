package com.company;

import com.company.requests.IHttpRequest;
import com.company.statistics.Type;
import com.company.threads.RequestAccessFactory;
import com.company.threads.RequestLimitFactory;
import com.company.requests.HttpGetRequestPage;
import com.company.settings.Settings;
import com.company.statistics.Statistics;
import com.company.threads.RequestWrapper;

import java.net.MalformedURLException;
import java.net.URL;

public class App {

    public static void main(String[] args) {

        if (args.length != 0 && args[0] != null) {
            executeArguments(args);
        } else {
            startStandardTestAccess();
        }

    }

    private static void executeArguments(String[] args) {

        String firstArg = args[0];
        switch (firstArg) {
            case "as":
                startStandardTestAccess();
                break;
            case "ls":
                startStandardTestByTime();
                break;
            case "ac":
                try {
                    startCustomTestAccess(new URL(args[1]), Integer.valueOf(args[2]));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case "lc":
                try {
                    startCustomTestByTime(new URL(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case "gp":
                startGeometricProgressionTestByTime();
                break;
            case "help":
                printOnScreenHelp();
                break;
            default:
                System.exit(0);
                break;
        }

    }

    public static void printOnScreenHelp() {

        System.out.println("java -jar jarfile [-options] [args...]");
        System.out.println("where options include:");
        System.out.println("-as         standard test access, configuration from file");
        System.out.println(" example    java -jar jarfile as");
        System.out.println("-ac <url> <number of requests>");
        System.out.println("            custom test access, configuration from file");
        System.out.println(" example    java -jar jarfile ac https://www.google.com/ 300");
        System.out.println("-ls         standard test by the time, configuration from file");
        System.out.println(" example    java -jar jarfile ls");
        System.out.println("-lc <url> <number requests per second> <test duration in seconds>");
        System.out.println("            custom test by the time, configuration from file");
        System.out.println(" example    java -jar jarfile ac https://www.google.com/ 500 10");
        System.out.println("-gp         standard geometric progression test by the time , configuration from file");
        System.out.println(" example    java -jar jarfile gp");

    }

    public static void startStandardTestAccess() {

        String url = Settings.getInstance().getProperty("url");

        IHttpRequest requestGet = new HttpGetRequestPage(url);
        Statistics.getInstance().setValue(Type.Url, url);

        RequestWrapper wrapper = new RequestWrapper(requestGet);

        RequestAccessFactory threadFactory = new RequestAccessFactory(wrapper);
        threadFactory.runAll();

    }

    public static void startStandardTestByTime() {

        IHttpRequest getRequest = new HttpGetRequestPage(Settings.getInstance().getProperty("url"));
        Statistics.getInstance().setValue(Type.Url, Settings.getInstance().getProperty("url"));

        RequestWrapper wrapper = new RequestWrapper(getRequest);

        RequestLimitFactory limitFactory = new RequestLimitFactory(wrapper);
        limitFactory.runAll();

    }

    public static void startCustomTestAccess(URL url, int numberOfThreads) {

        Statistics.getInstance().setValue(Type.Url, url.toString());

        IHttpRequest getRequest = new HttpGetRequestPage(url);
        RequestWrapper wrapper = new RequestWrapper(getRequest);

        RequestAccessFactory threadFactory = new RequestAccessFactory(wrapper);
        threadFactory.setNumberRequestsPerSecond(numberOfThreads);
        threadFactory.runAll();

    }

    public static void startCustomTestByTime(URL url, int numberRequestsPerSecond, int testDurationInSeconds) {

        Statistics.getInstance().setValue(Type.Url, url.toString());

        IHttpRequest getRequest = new HttpGetRequestPage(url);
        RequestWrapper wrapper = new RequestWrapper(getRequest);

        RequestLimitFactory limitFactory = new RequestLimitFactory(wrapper);
        limitFactory.setNumberRequestsPerSecond(numberRequestsPerSecond);
        limitFactory.setTestDurationInSeconds(testDurationInSeconds);
        limitFactory.runAll();

    }

    public static void startGeometricProgressionTestByTime() {

        IHttpRequest getRequest = new HttpGetRequestPage(Settings.getInstance().getProperty("url"));

        for (int i = 1; i <= 3; i++) {
            RequestWrapper wrapper = new RequestWrapper(getRequest);
            RequestLimitFactory limitFactory = new RequestLimitFactory(wrapper);
            limitFactory.setTestDurationInSeconds(Integer.valueOf(Settings.getInstance().getProperty("testDurationInSeconds")));
            limitFactory.setNumberRequestsPerSecond(Integer.valueOf(Settings.getInstance().getProperty("numberRequestsPerSecond")) * i);
            limitFactory.runAll();
        }

    }

}
