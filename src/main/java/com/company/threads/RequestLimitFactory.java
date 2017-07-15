package com.company.threads;

import com.company.logs.Logs;
import com.company.logs.HttpLogWriterTxt;
import com.company.logs.IHttpLogWriter;
import com.company.responses.HttpResponse;
import com.company.responses.IHttpResponse;
import com.company.settings.Settings;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RequestLimitFactory {

    private RequestWrapper requestInsideWrapper;

    private int numberRequestsPerSecond;
    private int testDurationInSeconds;

    private final Semaphore[] semaphores = new Semaphore[60];

    {
        numberRequestsPerSecond = Integer.valueOf(Settings.getInstance().getProperty("numberRequestsPerSecond"));
        testDurationInSeconds = Integer.valueOf(Settings.getInstance().getProperty("testDurationInSeconds"));
        InitializeListOfSemaphores();
    }

    public RequestLimitFactory(RequestWrapper wrapper) {
        this.requestInsideWrapper = wrapper;
    }

    public void setNumberRequestsPerSecond(int numberRequestsPerSecond) {
        this.numberRequestsPerSecond = numberRequestsPerSecond;
        InitializeListOfSemaphores();
    }

    public void setTestDurationInSeconds(int testDurationInSeconds) {
        this.testDurationInSeconds = testDurationInSeconds;
    }

    public void runAll() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        IHttpLogWriter logWriter = new HttpLogWriterTxt(IHttpLogWriter.getNewLogFileByDefault("test of time"));

        writePropertiesFromFileToLog(logWriter);

        logWriter.add(String.format("Internal setting: Current date %s", dateFormat.format(new Date())));
        Date startTime = getStartTimeOfExecution();
        Date endTime = getEndTimeOfExecution(startTime);
        logWriter.add(String.format("Internal setting: (Start - End) %s - %s", dateFormat.format(startTime), dateFormat.format(endTime)));

        Thread recoveryAgent = new Thread(new Runnable() {
            @Override
            public void run() {
                recoverySemaphores();
            }
        });
        recoveryAgent.start();

        IHttpResponse cachedResponse = new HttpResponse();
        cachedResponse.setStartTime(new Date());
        requestInsideWrapper.setCustomResponse(cachedResponse);

        logWriter.add(String.format("Start of test %s (waiting... %s) ", dateFormat.format(new Date()), timeFormat.format(startTime)), true);
        while (requestInsideWrapper.getThreadCount() < (numberRequestsPerSecond * testDurationInSeconds)) {
            if (!mayBeExecutedAtThisTime(startTime)) {
                continue;
            }

            // Sending a request
            if (mayBeExecutedAtThisTime(startTime, endTime) && semaphoreCountIsAvailable()) {
                new Thread(requestInsideWrapper).start();
            }

        }

        // The test is complete
        Map<String, IHttpResponse> groups = cachedResponse.getChildResponseGroups();
        Map<String, IHttpResponse> treeMap = new TreeMap<String, IHttpResponse>(groups);
        showOnScreenCompletedGroup(treeMap, logWriter);
        logWriter.add(String.format("End of test %s %s", requestInsideWrapper.getThreadCount(), dateFormat.format(new Date()), timeFormat.format(endTime)), true);
        logWriter.add("", true);
        recoveryAgent.interrupt();

    }

    public void InitializeListOfSemaphores() {

        for (int i = 0; i < semaphores.length; i++) {
            semaphores[i] = new Semaphore(numberRequestsPerSecond);
        }
    }

    public void recoverySemaphores() {

        while (!Thread.currentThread().isInterrupted()) {
            int currentSecond = getCurrentSecond();
            if (currentSecond != 0 && currentSecond % 10 != 0 && currentSecond != 59) {
                continue;
            }

            int counter = 10;

            for (int i = 59; i > currentSecond && counter != 0; i--) {
                semaphores[i] = new Semaphore(numberRequestsPerSecond);
                counter--;
            }

            for (int i = 0; i < currentSecond && counter != 0; i++) {
                semaphores[i] = new Semaphore(numberRequestsPerSecond);
                counter--;
            }

        }

    }

    public void showOnScreenCompletedGroup(Map<String, IHttpResponse> responseMap, IHttpLogWriter logWriter) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        for (Object object : responseMap.entrySet()) {
            Map.Entry<String, IHttpResponse> groups = (Map.Entry<String, IHttpResponse>) object;

            IHttpResponse responseParent = groups.getValue();

            logWriter.add(
                    String.format("%s responses %s - %s (Average response time %s)",
                            responseParent.getNumberOfChildResponses(),
                            timeFormat.format(responseParent.getStartTime()),
                            timeFormat.format(responseParent.getEndTime()),
                            responseParent.getAverageTimeInMilliseconds() + "ms"), true);

        }

    }


    public boolean semaphoreCountIsAvailable() {

        Semaphore semaphore = semaphores[getCurrentSecond()];

        try {
            return semaphore.tryAcquire(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;

    }

    private int getCurrentSecond() {
        return (int) (System.currentTimeMillis() / 1000) % 60;
    }

    public Date getStartTimeOfExecution() {

        Date currentDate = new Date();

        return new Date(
                currentDate.getYear(),
                currentDate.getMonth(),
                currentDate.getDate(),
                currentDate.getHours(),
                currentDate.getMinutes(),
                currentDate.getSeconds() + 1);

    }

    public Date getEndTimeOfExecution(Date startTime) {

        return new Date(
                startTime.getYear(),
                startTime.getMonth(),
                startTime.getDate(),
                startTime.getHours(),
                startTime.getMinutes(),
                startTime.getSeconds() + testDurationInSeconds);

    }

    public boolean mayBeExecutedAtThisTime(Date startTime, Date endTime) {
        return startTime.getTime() <= System.currentTimeMillis() && endTime.getTime() > System.currentTimeMillis();
    }

    public boolean mayBeExecutedAtThisTime(Date startTime) {
        return (System.currentTimeMillis() / 1000) >= (startTime.getTime() / 1000);
    }

    void writePropertiesFromFileToLog(IHttpLogWriter logWriter) {

        IHttpLogWriter log = null;
        if (logWriter == null) {
            log = Logs.getInstance();
        } else {
            log = logWriter;
        }

        log.add("url " + requestInsideWrapper.getRequest().getUrl(), true);
        log.add("numberRequestsPerSecond " + numberRequestsPerSecond, true);
        log.add("testDurationInSeconds " + testDurationInSeconds, true);
        log.add("logFolder " + Settings.getInstance().getProperty("logFolder"), true);
        log.add("outputFolder " + Settings.getInstance().getProperty("outputFolder"), true);

    }

}
