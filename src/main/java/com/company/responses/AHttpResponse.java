package com.company.responses;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AHttpResponse implements IHttpResponse {

    private long startTimeMillis = 0;

    private String text;
    private int code;

    private long endTimeMillis = 0;

    private Map<String, IHttpResponse> groupChildResponses = new HashMap();

    private ArrayList<IHttpResponse> childResponses = new ArrayList<>();

    public synchronized void addChildResponseInGroup(String group, IHttpResponse response) {

        if (!groupChildResponses.containsKey(group)) {
            HttpResponse newGroupResponse = new HttpResponse();
            newGroupResponse.setStartTime(response.getStartTime());

            groupChildResponses.put(group, newGroupResponse);
        }

        IHttpResponse groupResponse = groupChildResponses.get(group);

        if (groupResponse.getEndTime().getTime() < response.getEndTime().getTime()) {
            groupResponse.setEndTime(response.getEndTime());
        }

        groupResponse.addChildResponse(response);

    }

    @Override
    public Map getChildResponseGroups() {
        return groupChildResponses;
    }

    public AHttpResponse() {
    }

    public AHttpResponse(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Date getStartTime() {

        return new Date(startTimeMillis);
    }

    public void setStartTime(Date startTime) {
        this.startTimeMillis = startTime.getTime();
    }

    public Date getEndTime() {
        return new Date(endTimeMillis);
    }

    public void setEndTime(Date endTime) {
        this.endTimeMillis = endTime.getTime();
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public long getDifferenceTimeInMilliseconds() {
        return endTimeMillis - startTimeMillis;
    }

    @Override
    public void addChildResponse(IHttpResponse child) {
        childResponses.add(child);
    }

    @Override
    public int getNumberOfChildResponses() {
        return childResponses.size();
    }

    @Override
    public long getAverageTimeInMilliseconds() {

        if (getNumberOfChildResponses() != 0) {

            long amountOfMilliseconds = 0;
            for (IHttpResponse response : childResponses) {
                try {
                    amountOfMilliseconds += response.getDifferenceTimeInMilliseconds();
                } catch (NullPointerException e) {

                }
            }

            return amountOfMilliseconds / getNumberOfChildResponses();
        }

        return 0;
    }

}
