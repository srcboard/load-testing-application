package com.company.statistics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class AHttpStatisticsWriter implements IHttpStatisticsWriter {

    private Document document;

    private Map<Type, Integer> counters = new HashMap();
    private Map<Type, String> moreInfo = new HashMap();

    protected AHttpStatisticsWriter() {

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.newDocument();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        }

    }

    @Override
    public void setValue(Type k, Integer v) {
        counters.put(k, v);
    }

    @Override
    public void setValue(Type k, String v) {
        moreInfo.put(k, v);
    }

    @Override
    public void addOne(Type type) {

        if (!counters.containsKey(type)) {
            counters.put(type, 0);
        }

        int value = counters.get(type);
        value++;
        counters.replace(type, value);

    }

    @Override
    public int getCounter(Type type) {
        if (!counters.containsKey(type)) {
            counters.put(type, 0);
        }
        return counters.get(type);
    }

    @Override
    public String getInfo(Type type) {
        if (!moreInfo.containsKey(type)) {
            moreInfo.put(type, "");
        }
        return moreInfo.get(type);
    }

    @Override
    public void writeFile(String nameOfStatisticsFile) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        Element root = document.createElement("statistics");

        for (Object pair : moreInfo.entrySet()) {
            Map.Entry infoEntry = (Map.Entry<Type, String>) pair;

            Element element = document.createElement(infoEntry.getKey().toString());
            element.setTextContent(infoEntry.getValue().toString());
            root.appendChild(element);
        }

        for (Object pair : counters.entrySet()) {
            Map.Entry counterEntry = (Map.Entry<Type, Integer>) pair;

            Element element = document.createElement(counterEntry.getKey().toString());
            element.setTextContent(counterEntry.getValue().toString());
            root.appendChild(element);
        }

        document.appendChild(root);

        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);

            FileOutputStream fos = new FileOutputStream(IHttpStatisticsWriter.getNewStatisticsFileByDefault(nameOfStatisticsFile));

            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }

    }

}
