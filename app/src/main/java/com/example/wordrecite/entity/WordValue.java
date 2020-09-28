package com.example.wordrecite.entity;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * 用于存放单词的信息
 * 解析完的XML的信息存放到该类
 * */

public class WordValue {

    public String word,
            phE,              //英式音标
            phA,              //美式音标
            translation,      //翻译
            audio;            //发音url

    public String[] explain;          //官方释义

    public String [][] keyValue;


    public WordValue(String word, String phE, String phA,String[] explain,
                     String translation,  String audio,String [][] keyValue) {
        super();
        this.word = ""+ word;
        this.phE = ""+ phE;
        this.phA = ""+ phA;
        this.translation = ""+ translation;
        this.audio = ""+ audio;
        this.explain = new String[explain.length];
        this.keyValue = new String[keyValue.length][];
        for (int i = 0; i < explain.length; i++) {
            this.explain[i] = explain[i];
        }
        for (int i = 0; keyValue[i][0] != null; i++) {
            this.keyValue[i] = new String[keyValue[i].length];
            for(int j = 0; keyValue[i][j] != null; j++) {
                this.keyValue[i][j] = keyValue[i][j];
            }
        }
    }

    public WordValue() {
        super();
        this.word = "";             //防止空指针异常
        this.phE = "";
        this.phA = "";
        this.translation = "";
        this.audio = "";
        this.explain = new String[100];
        this.keyValue= new String[100][100];
    }

    public  String[][] getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String[][] keyValue) {
        for (int i = 0; keyValue[i][0] != null; i++) {
            for(int j = 0; keyValue[i][j] != null; j++) {
                this.keyValue[i][j] = keyValue[i][j];
            }
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhE() {
        return phE;
    }

    public void setPhE(String phE) {
        this.phE = phE;
    }

    public String getPhA() {
        return phA;
    }

    public void setPhA(String phA) {
        this.phA = phA;
    }

    public String getAudio() { return audio; }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
    public  String[] getExplain() {
        return explain;
    }

    public void setExplain(String explain[]) {
        for (int i = 0; explain[i] != null; i++) {
            this.explain[i] = explain[i];
        }
    }



    public void printInfo(){
        System.out.println(this.word);
        System.out.println(this.phE);
        System.out.println(this.phA);
        System.out.println(this.translation);
        System.out.println(this.audio);
        for (int i = 0;this.explain[i] != null; i++) {
            System.out.println(this.explain[i]);
        }
        for (int i = 0; keyValue[i][0] != null; i++) {
            for(int j = 0; keyValue[i][j] != null; j++) {
                System.out.println(keyValue[i][j]);
            }
        }
    }
}