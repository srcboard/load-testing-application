package com.company.requests;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AHttpUrlGetRequest implements IHttpGetRequest {

    private URL url;

    public AHttpUrlGetRequest(URL url) {
        this.url = url;
    }

    public AHttpUrlGetRequest(String url) {

        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public URL getUrl() {
        return url;
    }

}
