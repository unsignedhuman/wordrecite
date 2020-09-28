package com.example.wordrecite.xml;

import com.example.wordrecite.entity.WordValue;
import com.example.wordrecite.handler.YouDaoContentHandler;
import com.example.wordrecite.net.NetOperator;

import org.xml.sax.InputSource;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetWordFromInternetImpl implements GetWordFromInternet{
    public WordValue getWordFromInternet(String searchedWord){
        WordValue wordValue=null;
        if(searchedWord==null&& searchedWord.equals(""))
            return null;
        char[] array=searchedWord.toCharArray();
        InputStream in=null;
        String str=null;
        try{
            String tempUrl= NetOperator.youDaoURL1+searchedWord;
            in=NetOperator.getInputStreamByUrl(tempUrl);   //从网络获得输入流
            if(in!=null){
                XMLParser xmlParser=new XMLParser();
                InputStreamReader reader=new InputStreamReader(in,"utf-8");        //最终目的获得一个InputSource对象用于传入形参
                YouDaoContentHandler contentHandler=new YouDaoContentHandler();
                xmlParser.parseYouDaoXml(contentHandler, new InputSource(reader));
                wordValue=contentHandler.getWordValue();
                wordValue.setWord(searchedWord);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return wordValue;
    }
}
