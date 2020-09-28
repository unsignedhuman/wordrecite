package com.example.wordrecite.wordcontainer;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.LinkedList;

import org.xml.sax.InputSource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.wordrecite.database.DataBaseHelperDict;
import com.example.wordrecite.entity.WordValue;
import com.example.wordrecite.net.NetOperator;
import com.example.wordrecite.handler.YouDaoContentHandler;
import com.example.wordrecite.xml.XMLParser;
import com.example.wordrecite.utils.FileUtils;

public class Dict {

    public Context context=null;
    public String dataBaseName=null;
    private DataBaseHelperDict dbHelper=null;
    private SQLiteDatabase dbR=null,dbW=null;


    public Dict(Context context,String dataBaseName){
        this.context=context;
        this.dataBaseName=dataBaseName;
        dbHelper=new DataBaseHelperDict(context, dataBaseName);                                                                      //并且调用下面两个方法获得dbR和dbW,用于完成对数据库的增删改查操作。                                                                 //这里吧dbR dbW作为成员变量目的是避免反复实例化dbR  dbW造成数据库指针泄露。
        dbR=dbHelper.getReadableDatabase();
        dbW=dbHelper.getWritableDatabase();

    }


    @Override
    protected void finalize() throws Throwable {      //在该对象销毁时，释放dbR和dbW
        // TODO Auto-generated method stub
        dbR.close();
        dbW.close();
        dbHelper.close();
        super.finalize();

    }

    //WordValue对象添加进数据库，使用dbW的insert方法，需要创建一个ContentValue对象存放键值对
    public void insertWordToDict(WordValue w, boolean isOverWrite){
        if(w==null){          //避免空指针异常
            return;
        }
        Cursor cursor=null;
        try{
            ContentValues values=new ContentValues();
            ContentValues values_KV=new ContentValues();
            values.put("word",w.getWord() );
            values.put("phA", w.getPhA());
            values.put("phE",w.getPhE());
            values.put("audio", "http://dict.youdao.com/dictvoice?audio=" + w.getWord());
            values.put("translation", w.getTranslation());
            //将String[] 转换为String
            String[] explain = w.getExplain();
            String explains = new String();
            for (int i = 0; i < explain.length; i++) {
                explains += explain[i];
            }
            values.put("explains", explains);
            cursor=dbR.query("wordList", new String[]{"word"}, "word=?", new String[]{w.getWord()}, null, null, null);
            if(cursor.getCount()>0){
                if(isOverWrite==false)
                    return;
                else{
                    dbW.update("wordList", values, "word=?",new String[]{ w.getWord()});
                }
            }else{
                dbW.insert("wordList", null, values);  //这里可能会发生空指针异常，到时候考虑
            }
            cursor=dbR.query("wordList", new String[]{"word"}, "word=?", new String[]{w.getWord()}, null, null, null);
            Integer wordId = cursor.getInt(cursor.getColumnIndex("id"));
            String[][] keyValue = w.getKeyValue();
            for (int i = 0; keyValue[i][0] != null; i++) {
                for(int j = 1; keyValue[i][j] != null; j++) {
                    values_KV.put("word_id",wordId);
                    values_KV.put("word_key",keyValue[i][0]);
                    values_KV.put("word_value",keyValue[i][j]);
                    cursor=dbR.query("webKV", new String[]{"word_key","word_id"}, "word_key=?,word_id=?", new String[]{keyValue[i][0],wordId.toString()}  , null, null, null);
                    if(cursor.getCount()>0){
                        if(isOverWrite==false)
                            return;
                        else{
                            dbW.update("webKV", values_KV, "word_key=?,word_id=?",new String[]{ keyValue[i][0],wordId.toString()});
                        }
                    }else{
                        dbW.insert("webKV", null, values_KV);  //这里可能会发生空指针异常，到时候考虑
                    }
                }
            }
        }catch(Exception e){

        }finally{
            if(cursor!=null)
                cursor.close();
        }

    }
    //判断数据库中是否存在某个单词
    public boolean isWordExist(String word){
        Cursor cursor=null;
        try{
            cursor=dbR.query("wordList", new String[]{"word"}, "word=?", new String[]{word}, null, null, null);
            if(cursor.getCount()>0){
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }
        }finally{
            if(cursor!=null)
                cursor.close();
        }
    }

    //从单词库中获得某个单词的信息，如果词库中没有该单词，那么返回null
    public WordValue getWordFromDict(String searchedWord){

        WordValue w = new WordValue();
        String[] columns=new String[]{"id","word",
                "phE","phA","explains","translation","audio",};
        String[] columns_KV=new String[]{"word_key","word_value"};

        String[] strArray=new String[7];
        String[][] strKV = new String[100][100];
        Cursor cursor=dbR.query("wordList", columns, "word=?", new String[]{searchedWord}, null, null, null);
        while(cursor.moveToNext()){
            for(int i=0;i<strArray.length;i++){
                strArray[i]=cursor.getString(cursor.getColumnIndex(columns[i]));
            }
        }
        cursor=dbR.query("webKV", columns_KV, "word_id=?", new String[]{strArray[0]}, null, null, null);
        int i = 0,j = 1;
        while(cursor.moveToNext()){
            if(strKV[i][0] == null) {
                strKV[i][0] = cursor.getString(cursor.getColumnIndex("word_key"));
                strKV[i][j++]=cursor.getString(cursor.getColumnIndex("word_value"));
            }
            else if(cursor.getString(cursor.getColumnIndex("word_key")) != strKV[i][0]) {
                i++;
                j=1;
                strKV[i][0] = cursor.getString(cursor.getColumnIndex("word_key"));
                strKV[i][j++]=cursor.getString(cursor.getColumnIndex("word_value"));
            }
            else
                strKV[i][j++]=cursor.getString(cursor.getColumnIndex("word_value"));
        }
        cursor.close();
        //将String 转换为String[]
       String[] explain = strArray[4].split("\\\\n");
       w=new WordValue(strArray[1],strArray[2],strArray[3],explain,strArray[5],strArray[6],strKV);
       return w;

    }


   //从网络查找某个单词，并且返回一个含有单词信息的WordValue对象
    public WordValue getWordFromInternet(String searchedWord){
        WordValue wordValue=null;
        String tempWord=searchedWord;
        if(tempWord==null&& tempWord.equals(""))
            return null;
        char[] array=tempWord.toCharArray();
        if(array[0]>256)           //是中文，或其他语言的的简略判断
            tempWord=URLEncoder.encode(tempWord);
        InputStream in=null;
        String str=null;
        try{
            String tempUrl=NetOperator.youDaoURL1+tempWord;
            in=NetOperator.getInputStreamByUrl(tempUrl);
            if(in!=null){
                new FileUtils().saveInputStreamToFile(in, "", "youDaoXml.txt");
                XMLParser xmlParser=new XMLParser();
                InputStreamReader reader=new InputStreamReader(in,"utf-8");
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

    //获得某个单词的某一项信息，基本的思路还是先获得全部信息WordValue然后调用WordValue的get方法获得具体的信息。
    //获取发音文件地址
    public String getAudioUrl(String searchedWord){
        Cursor cursor=dbR.query("wordList", new String[]{"audio"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("audio"));
        cursor.close();
        return str;
    }

    //获取音标
    public String getPsEng(String searchedWord){
        Cursor cursor=dbR.query("wordList", new String[]{"phE"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("phE"));
        cursor.close();
        return str;
    }

    public String getPsUSA(String searchedWord){
        Cursor cursor=dbR.query("wordList", new String[]{"phA"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("phA"));
        cursor.close();
        return str;
    }


    /**
     * 若没有释义那么返回的链表的长度为0,若单词不存在那么直接返回null,所以最好对null和长度同时检验
     * @param searchedWord
     * @return
     */
    public String getexplain(String searchedWord){
        Cursor cursor=dbR.query("wordList", new String[]{"explains"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("explains"));
        cursor.close();
        return str;
    }
}
