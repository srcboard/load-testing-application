package com.company.logs;

import java.io.File;

public class HttpLogWriterTxt extends AHttpLogWriterTxt {

    public HttpLogWriterTxt() {
        super();
    }

    public HttpLogWriterTxt(File LogFile) {
        super(LogFile);
    }

}
