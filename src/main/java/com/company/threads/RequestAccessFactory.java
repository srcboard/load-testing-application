package com.company.threads;

import com.company.logs.Logs;
import com.company.responses.HttpResponse;
import com.company.responses.IHttpResponse;
import com.company.statistics.Type;
import com.company.logs.HttpLogWriterTxt;
import com.company.logs.IHttpLogWriter;
import com.company.settings.Settings;
import com.company.statistics.IHttpStatisticsWriter;
import com.company.statistics.Statistics;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestAccessFactory {

    private int numberRequestsPerSecond;

    private IHttpStatisticsWriter statistics;

    private ExecutorService service;
    private RequestWrapper requestWrapper;

    {
        numberRequestsPerSecond = Integer.valueOf(Settings.getInstance().getProperty("numberRequestsPerSecond"));
        this.statistics = Statistics.getInstance();
        this.service = Executors.newFixedThreadPool(500);
    }

    public void setNumberRequestsPerSecond(int numberRequestsPerSecond) {
        this.numberRequestsPerSecond = numberRequestsPerSecond;
    }

    public RequestAccessFactory(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
    }

    public void runAll() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        IHttpResponse customResponse = new HttpResponse();
        requestWrapper.setCustomResponse(customResponse);
        IHttpLogWriter customLog = new HttpLogWriterTxt(IHttpLogWriter.getNewLogFileByDefault("test availability"));
        requestWrapper.setCustomLog(customLog);

        writePropertiesFromFileToLog(customLog);

        customResponse.setStartTime(new Date());
        customLog.add(String.format("Test availability started %s ...", dateFormat.format(new Date())), true);
        statistics.setValue(Type.StartTime, dateFormat.format(new Date()));
        statistics.setValue(Type.Url, requestWrapper.getRequest().getUrl().toString());

        // Preparation of requests wrappers
        List<Callable<RequestWrapper>> wrapperList = new ArrayList<>();
        for (int i = 0; i < numberRequestsPerSecond; i++) {
            wrapperList.add(requestWrapper);
        }

        try {
            List answers = service.invokeAll(wrapperList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, IHttpResponse> groupsResponses = customResponse.getChildResponseGroups();
        Map<String, IHttpResponse> treeMap = new TreeMap(groupsResponses);
        showOnScreenCompletedGroup(treeMap, customLog);

        int allNumberRequests = 0;
        int allTimeRequests = 0;
        for (Object object : groupsResponses.entrySet()) {
            Map.Entry<String, IHttpResponse> group = (Map.Entry<String, IHttpResponse>) object;

            IHttpResponse responseParent = group.getValue();
            allNumberRequests += responseParent.getNumberOfChildResponses();
            allTimeRequests += responseParent.getAverageTimeInMilliseconds();
        }

        statistics.setValue(Type.NumberRequests, allNumberRequests);

        if (allNumberRequests != 0 && allTimeRequests != 0) {
            statistics.setValue(Type.AverageRequestTime, allTimeRequests / allNumberRequests);
        }

        customResponse.setEndTime(new Date());
        customLog.add(String.format("Test availability ended %s ...", dateFormat.format(new Date())), true);
        statistics.setValue(Type.EndTime, dateFormat.format(new Date()));
        statistics.setValue(Type.TotalTime, (int) customResponse.getDifferenceTimeInMilliseconds());

        statistics.writeFile("test availability");
        service.shutdownNow();

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

    void writePropertiesFromFileToLog(IHttpLogWriter logWriter) {

        IHttpLogWriter log = null;
        if (logWriter == null) {
            log = Logs.getInstance();
        } else {
            log = logWriter;
        }

        log.add("url " + requestWrapper.getRequest().getUrl(), true);
        log.add("numberRequestsPerSecond " + numberRequestsPerSecond, true);
        log.add("logFolder " + Settings.getInstance().getProperty("logFolder"), true);
        log.add("outputFolder " + Settings.getInstance().getProperty("outputFolder"), true);

    }

}
