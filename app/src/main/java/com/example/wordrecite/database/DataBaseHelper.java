package com.example.wordrecite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wordrecite.entity.WordValue;

public class DataBaseHelper extends SQLiteOpenHelper {

    public Context context = null;
    public String dataBaseName = null;
    public static int VERSION = 1;

    public DataBaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.dataBaseName = name;
    }

    public DataBaseHelper(Context context, String name, CursorFactory factory) {
        this(context, name, factory, VERSION);
        this.context = context;
        this.dataBaseName = name;
    }

    public DataBaseHelper(Context context, String name) {
        this(context, name, null);
        this.context = context;
        this.dataBaseName = name;
    }

    ;


    @Override //数据库第一次创建时使用
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table wordList_glossary(id integer primary key autoincrement," +
                "word text,phA text,phE text,audio text," +
                "translation text, explains text)");
    }

    @Override //更新时使用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    private SQLiteDatabase dbR = null, dbW = null;

    //WordValue对象添加进数据库，使用dbW的insert方法，需要创建一个ContentValue对象存放键值对
    public boolean insertWordInfoToDataBase(WordValue w, boolean isOverWrite) {
        if (w == null) {          //避免空指针异常
            return false;
        }
        Cursor cursor = null;
        try {
            ContentValues values = new ContentValues();
            ContentValues values_KV = new ContentValues();
            values.put("word", w.getWord());
            values.put("phA", w.getPhA());
            values.put("phE", w.getPhE());
            values.put("audio", "http://dict.youdao.com/dictvoice?audio=" + w.getWord());
            values.put("translation", w.getTranslation());
            //将String[] 转换为String
            String[] explain = w.getExplain();
            String explains = new String();
            for (int i = 0; i < explain.length; i++) {
                explains += explain[i];
            }
            values.put("explains", explains);
            cursor = dbR.query("wordList", new String[]{"word"}, "word=?", new String[]{w.getWord()}, null, null, null);
            if (cursor.getCount() > 0) {
                if (isOverWrite == false)
                    return false;
                else {
                    dbW.update("wordList", values, "word=?", new String[]{w.getWord()});
                }
            } else {
                dbW.insert("wordList", null, values);  //这里可能会发生空指针异常，到时候考虑
            }

        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return true;
    }
}