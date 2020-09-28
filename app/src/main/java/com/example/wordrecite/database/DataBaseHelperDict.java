package com.example.wordrecite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelperDict extends SQLiteOpenHelper{

    public Context context=null;
    public String dataBaseName=null;
    public static int VERSION=1;

    public DataBaseHelperDict(Context context, String name, CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.dataBaseName=name;
    }

    public DataBaseHelperDict(Context context, String name, CursorFactory factory){
        this(context,name,factory,VERSION);
        this.context=context;
        this.dataBaseName=name;
    }

    public DataBaseHelperDict(Context context, String name){
        this(context,name,null);
        this.context=context;
        this.dataBaseName=name;
    };


    @Override //数据库第一次创建时使用
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table wordList(id integer primary key autoincrement," +
                "word text,phA text,phE text,audio text," +
                "translation text, explains text)");

        db.execSQL("create table webKV(id integer primary key autoincrement," +
                "word_id integer,word_key text,word_value text)");
    }

    @Override //更新时使用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }



}