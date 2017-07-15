package com.company.requests;

import com.company.responses.HttpResponse;

import java.net.URL;

public interface IHttpRequest {

    URL getUrl();

    HttpResponse send();

}
