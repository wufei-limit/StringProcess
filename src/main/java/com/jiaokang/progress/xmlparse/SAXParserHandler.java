package com.jiaokang.progress.xmlparse;

import com.jiaokang.progress.TaskDataQueue;
import com.jiaokang.progress.entities.StringLocalData;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by jiaokang on 2022/2/25
 */
public class SAXParserHandler extends DefaultHandler {
    private final StringBuilder currentValue = new StringBuilder();
    private String currentTag = null;
    private static final String STRING_TAG = "string";
    private static final String NAME_TAG = "name";
    private boolean isSkipElement = false;
    private String mLocal = null;
    private String mModule = null;

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!qName.equals(STRING_TAG)) {
            isSkipElement = true;
            return;
        }
        String value = attributes.getValue(NAME_TAG);
        if (null == value || value.isEmpty()) {
            isSkipElement = true;
            return;
        }
        currentValue.setLength(0);
        isSkipElement = false;
        currentTag = value;
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isSkipElement) {
            return;
        }
        isSkipElement = true; //防止最后一个子标签跟随父标签的结束
        StringLocalData localData = new StringLocalData(mModule,mLocal,currentTag,currentValue.toString());
        TaskDataQueue.putData(localData);
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isSkipElement) {
            return;
        }
        currentValue.append(ch, start, length);
    }

    public void setLocal(String mLocal) {
        this.mLocal = mLocal;
    }

    public void setModule(String module) {
        this.mModule = module;
    }

}
