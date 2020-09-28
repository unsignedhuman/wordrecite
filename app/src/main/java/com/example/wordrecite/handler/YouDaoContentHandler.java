package com.example.wordrecite.handler;

import com.example.wordrecite.entity.WordValue;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 用于解析XML
 * */

public class YouDaoContentHandler extends DefaultHandler {

    public WordValue wordValue = null;
    private String tagName = null;
    private String[] explain = new String[100];
    private String flag = "";
    private String[][] kv = new String[100][100];
    private int keyNum = 0, valueNum = 1, explainNum=0;

    public YouDaoContentHandler() {
        wordValue = new WordValue();
    }

    public WordValue getWordValue() {
        return wordValue;
    }

    /**
     * 解析标签的内容时调用
     * ch：当前读取到的文本节点的字节数组
     * start：字节开始的位置，为0则读取全部
     * length：当前TextNode的长度
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        super.characters(ch, start, length);
        if (length <= 0)
            return;
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '\n')
                return;
        }

        String str = new String(ch, start, length);
        if (tagName == "query") {
            wordValue.setWord(str);
        } else if (tagName == "paragraph") {
            wordValue.setTranslation(str);
        } else if (tagName == "us-phonetic") {
            wordValue.setPhA(str);
        } else if (tagName == "uk-phonetic") {
            wordValue.setPhE(str);
        } else if (tagName == "ex") {
            switch (flag) {
                case "Explains": {
                    explain[explainNum++] = str;
                    break;
                }
                case "Values": {
                    kv[keyNum-1][valueNum++] = str;
                    break;
                }
            }
        } else if (tagName == "key") {
            kv[keyNum++][0] = str;
            valueNum = 1;
        }
    }

    /**
     * 标签开解析结束后调用
     **/
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, qName);
        tagName = null;
    }

    /**
     * 标签开解析前调用
     * uri：xml文档的命名空间
     * localName: 标签的名字
     * qName：带命名空间的标签的名字
     * attributes：标签的属性集
     **/
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        super.startElement(uri, localName, qName, attributes);
        tagName = qName;
        if(tagName == "value")
            flag="Values";
        if (tagName == "explains")
            flag = "Explains";
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        wordValue.setExplain(explain);
        wordValue.setKeyValue(kv);
    }
}




