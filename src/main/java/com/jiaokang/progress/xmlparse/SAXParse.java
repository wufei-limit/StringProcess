package com.jiaokang.progress.xmlparse;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by jiaokang on 2022/2/25
 */
public class SAXParse {
    private static  SAXParser mParser = null;
    private static  SAXParserHandler handler = null;

    public static void init(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            mParser = factory.newSAXParser();
            handler = new SAXParserHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void parse(String module , String local, File file){
        try {
            handler.setLocal(local);
            handler.setModule(module);
            mParser.parse(file,handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
