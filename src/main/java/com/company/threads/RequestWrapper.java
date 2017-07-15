package com.company.threads;

import com.company.logs.Logs;
import com.company.requests.IHttpRequest;
import com.company.responses.IHttpResponse;
import com.company.logs.IHttpLogWriter;
import com.company.statistics.IHttpStatisticsWriter;
import com.company.statistics.Statistics;

import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

public class RequestWrapper implements Callable, Runnable {

    public static final int OK_RESPONSE_CODE = 200;

    private IHttpLogWriter customLog;
    private IHttpResponse customResponse;

    private IHttpStatisticsWriter statistics;

    public IHttpRequest getRequest() {
        return request;
    }

    private IHttpRequest request;

    private int threadCount = 0;

    {
        this.statistics = Statistics.getInstance();
    }

    public synchronized int getThreadCount() {
        return this.threadCount;
    }

    private synchronized void incrementThreadCount() {
        this.threadCount++;
    }

    public synchronized void resetThreadCount() {
        this.threadCount = 0;
    }

    public RequestWrapper(IHttpRequest request) {
        this.request = request;
    }

    public void setCustomLog(IHttpLogWriter customLog) {
        this.customLog = customLog;
    }

    public void setCustomResponse(IHttpResponse customResponse) {
        this.customResponse = customResponse;
    }

    @Override
    public IHttpResponse call() throws Exception {
        return sendRequest();
    }

    @Override
    public void run() {
        sendRequest();
    }

    public IHttpResponse sendRequest() {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        IHttpResponse response = request.send();

        incrementThreadCount();

        IHttpLogWriter log = null;
        if (customLog == null) {
            log = Logs.getInstance();
        } else {
            log = customLog;
        }

        if (response.getCode() == OK_RESPONSE_CODE) {

            if (customResponse != null) {
                customResponse.addChildResponseInGroup(String.valueOf(response.getStartTime().getTime() / 1000), response);
            }

            if (customLog != null) {
                log.add(String.format("Successful response %s %s - %s (%s)",
                        getThreadCount(),
                        timeFormat.format(response.getStartTime()),
                        timeFormat.format(response.getEndTime()),
                        response.getDifferenceTimeInMilliseconds() + "ms"), true);
            }

        }

        return response;

    }

}

