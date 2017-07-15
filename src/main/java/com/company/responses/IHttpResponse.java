package com.company.responses;

import java.util.Date;
import java.util.Map;

public interface IHttpResponse {

    Integer getCode();

    String getText();

    Date getStartTime();

    void setStartTime(Date date);

    Date getEndTime();

    void setEndTime(Date date);

    long getDifferenceTimeInMilliseconds();

    void addChildResponse(IHttpResponse child);

    int getNumberOfChildResponses();

    long getAverageTimeInMilliseconds();

    void addChildResponseInGroup(String group, IHttpResponse response);

    Map getChildResponseGroups();

}
