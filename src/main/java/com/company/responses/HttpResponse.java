package com.company.responses;

public class HttpResponse extends AHttpResponse {

    public HttpResponse() {
        super();
    }

    public HttpResponse(Integer code, String text) {
        super(code, text);
    }

}
