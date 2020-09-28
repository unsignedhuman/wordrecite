package com.example.wordrecite.xml;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

public class XMLParser {
    public SAXParserFactory factory=null;
    public XMLReader reader=null;

    public XMLParser(){

        try {
            factory=SAXParserFactory.newInstance();
            reader=factory.newSAXParser().getXMLReader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseYouDaoXml(DefaultHandler content, InputSource inSource){
        if(inSource==null)
            return;
        try {
            reader.setContentHandler(content);
            reader.parse(inSource);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*public void parseDailySentenceXml(DailySentContentHandler contentHandler, InputSource inSource){
        if(inSource==null)
            return;
        try {
            reader.setContentHandler(contentHandler);
            reader.parse(inSource);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/


}