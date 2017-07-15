package com.company.requests;

import com.company.responses.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class HttpGetRequestPage extends AHttpUrlGetRequest {

    public HttpGetRequestPage(URL url) {
        super(url);
    }

    public HttpGetRequestPage(String url) {
        super(url);
    }

    @Override
    public HttpResponse send() {

        HttpResponse response = new HttpResponse();

        URLConnection connection = null;
        try {
            connection = getUrl().openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream stream = null;
        try {
            response.setStartTime(new Date());
            stream = connection.getInputStream();
            response.setEndTime(new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamReader reader = new InputStreamReader(stream);

        char[] buffer = new char[256];

        StringBuilder builder = new StringBuilder();

        int rc;
        try {
            while ((rc = reader.read(buffer)) != -1)
                builder.append(buffer, 0, rc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setCode(200);
        response.setText(builder.toString());

        return response;

    }

}

