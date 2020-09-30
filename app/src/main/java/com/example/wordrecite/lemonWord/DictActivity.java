package com.example.wordrecite.lemonWord;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordrecite.R;
import com.example.wordrecite.adapter.DictSentenceListAdapter;
import com.example.wordrecite.database.DataBaseHelper;
import com.example.wordrecite.entity.WordValue;
import com.example.wordrecite.music.Mp3Player;
import com.example.wordrecite.wordcontainer.Dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DictActivity extends AppCompatActivity {

    public TextView textDictWord=null;
    public TextView textDictPhonSymbolEng=null;
    public TextView textDictPhonSymbolUSA=null;
    public TextView textDictInterpret=null;
    public ListView listViewDictSentence=null;
    public ImageButton imageBtnDictAddToWordList=null;
    public ImageButton imageBtnDictAudio=null;
    public ImageButton imageBtnDictSerach=null;
    public ImageButton imageBtnDictBackToGeneral=null;
    public ImageButton imageBtnDictDelteEditText=null;

    public Button buttonDictDialogConfirm=null;
    public Button buttonDictDialogCancel=null;

    public EditText editTextDictSearch=null;

    public Dict dict=null;

    public WordValue w=null;

    public DataBaseHelper dbGlossaryHelper=null;
    public SQLiteDatabase dbGlossaryR=null;
    public SQLiteDatabase dbGlossaryW=null;


    public Mp3Player mp3Box=null;

    public static String searchedWord=null;

    public Handler dictHandler=null;

    public void initial(){
        textDictWord=(TextView) findViewById(R.id.text_dict_word);
        textDictInterpret=(TextView)findViewById(R.id.text_dict_interpret);
        textDictPhonSymbolEng=(TextView)findViewById(R.id.text_dict_phosymbol_eng);
        textDictPhonSymbolUSA=(TextView)findViewById(R.id.text_dict_phosymbol_usa);
        listViewDictSentence=(ListView)findViewById(R.id.listview_dict_sentence);

        imageBtnDictAddToWordList=(ImageButton)findViewById(R.id.image_btn_dict_add_to_word_list);
        imageBtnDictBackToGeneral=(ImageButton)findViewById(R.id.image_btn_dict_back_to_general);
        imageBtnDictAudio=(ImageButton)findViewById(R.id.image_btn_dict_audio);
        imageBtnDictSerach=(ImageButton)findViewById(R.id.image_btn_dict_search);
        imageBtnDictDelteEditText=(ImageButton)findViewById(R.id.image_btn_dict_delete_edit_text);


        editTextDictSearch=(EditText)findViewById(R.id.edit_text_dict);
        editTextDictSearch.setOnEditorActionListener(new EditTextDictEditActionLis());
        dict=new Dict(DictActivity.this, "dict");
        mp3Box=new Mp3Player(DictActivity.this, "dict");
        DataBaseHelper dbGlossaryHelper = new DataBaseHelper(DictActivity.this, "glossary");
        dbGlossaryR=dbGlossaryHelper.getReadableDatabase();
        dbGlossaryW=dbGlossaryHelper.getWritableDatabase();


        dictHandler=new Handler(Looper.getMainLooper());

        //对searchedWord进行初始化
        /*Intent intent=this.getIntent();
        searchedWord=intent.getStringExtra("word");
        if(searchedWord==null)
            searchedWord="";
        //设置查找的文本
        textDictWord.setText(searchedWord);*/
        this.searchedWord="compromise";
    }

    /**
     * 该方法可能需要访问网络，因此放在线程里进行
     * @param word
     */
    public void searchWord(String word){
        //调用该方法后首先初始化界面
        dictHandler.post(new RunnableDictSetTextInterface(word,"", "", null, null ));
        WordValue w = new WordValue();
        if (dict.isWordExist(word)==false){  //数据库中没有单词记录，从网络上进行同步
            w = dict.getWordFromInternet(word);

            if(w ==null ||w.getWord().equals("")){
                return;
            }
            //错词不添加进词典
            dict.insertWordToDict(w, true);   //默认添加到词典中
        }
        //能走到这一步说明从网上同步成功，数据库中一定存在单词记录
        else{
            w = dict.getWordFromDict(word);
        }

        if(w==null){             //这里又进一步做了保护,若词典中还是没有，那么用空字符串代替
            w=new WordValue();
        }

        String phoSymEng=w.getPhE();
        String phoSymUsa=w.getPhA();
        String[] explain=w.getExplain();
        String[][] keyValue=w.getKeyValue();

        dictHandler.post(new RunnableDictSetTextInterface(searchedWord, phoSymEng, phoSymUsa, explain, keyValue));
        if(phoSymEng.equals("")==false){    //只有有音标时才去下载音乐
            mp3Box.playMusicByWord(searchedWord, true, false);
        }
    }

    public void setOnClickLis(){
        imageBtnDictBackToGeneral.setOnClickListener(new IBDictBackToGeneralClickLis() );
        imageBtnDictAudio.setOnClickListener(new IBDictPlayMusicByAccentClickLis());
        imageBtnDictAddToWordList.setOnClickListener(new IBDictAddWordToGlossaryClickLis());
        imageBtnDictDelteEditText.setOnClickListener(new IBDictDeleteEditTextClickLis());
        imageBtnDictSerach.setOnClickListener(new IBDictSearchClickLis());
    }

    public void showAddDialog(){
        if(searchedWord==null)
            return;
        AlertDialog dialog=new AlertDialog.Builder(DictActivity.this).create();
        dialog.show();
        Window window=dialog.getWindow();
        window.setContentView(R.layout.layout_if_dialog);
        buttonDictDialogConfirm=(Button)window.findViewById(R.id.dialog_confirm);
        buttonDictDialogCancel=(Button)window.findViewById(R.id.dialog_cancel);
        buttonDictDialogConfirm.setOnClickListener(new BDictDialogConfirmClickLis(dialog));
        buttonDictDialogCancel.setOnClickListener(new BDictDialogCancelClickLis(dialog));
        TextView dialogText=(TextView)window.findViewById(R.id.dialog_text);
        dialogText.setText("把"+searchedWord+"添加到单词本?");

    }

    public void insertWordToGlossary(){
        if(w==null || w.getExplain().equals("")){
            Toast.makeText(DictActivity.this, "单词格式错误", Toast.LENGTH_SHORT).show();
            return;                   //若是不是有效单词，那么将不能添加到单词本
        }
        boolean isSuccess=dbGlossaryHelper.insertWordInfoToDataBase(w,false);
        if(isSuccess){
            Toast.makeText(DictActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(DictActivity.this, "单词已存在", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        initial();
        setOnClickLis();

        new ThreadDictSearchWordAndSetInterface().start();

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mp3Box.isMusicPermitted=true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mp3Box.isMusicPermitted=false;
        super.onPause();
    }


    public class ThreadDictSearchWordAndSetInterface extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            searchWord(searchedWord);
        }

    }

    public class RunnableDictSetTextInterface implements Runnable{
        String searchStr=null;
        String phoSymEng=null;
        String phoSymUSA=null;
        String[] explains=null;
        String[][] keyValue= null;


        public RunnableDictSetTextInterface(String searchStr, String phoSymEng,
                                            String phoSymUSA, String[] interpret, String[][] keyValue) {
            super();
            this.searchStr = searchStr;
            this.phoSymEng = "英["+phoSymEng+"]";
            this.phoSymUSA = "美["+phoSymUSA+"]";
            this.explains = interpret;
            this.keyValue = keyValue;
        }


        @Override
        public void run() {
            // TODO Auto-generated method stub
            textDictWord.setText(searchStr);
            textDictPhonSymbolEng.setText(phoSymEng);
            textDictPhonSymbolUSA.setText(phoSymUSA);
            String explain = new String();
            if(explains!=null) {
                for (int i = 0; i < explains.length; i++) {
                    explain += explains[i];
                }
            }else {
                explain ="";
            }
            if(keyValue==null){     //对链表为空进行防护
                //    textDictSentence.setText("");
                return;
            }

            ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
            for (int i = 0; keyValue[i] != null; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                List<String> webValue = new ArrayList<>();
                for (int j = 1; keyValue[i][j] != null; j++) {
                        webValue.add(keyValue[i][j]);
                }
                map.put(keyValue[i][0] ,  webValue);
                list.add(map);
            }

            DictSentenceListAdapter adapter=new DictSentenceListAdapter(DictActivity.this, R.layout.dict_sentence_list_item, list, new String[]{"phrase"}, new int[]{R.id.text_dict_sentence_list_item});
            listViewDictSentence.setAdapter(adapter);

        }

    }

    class IBDictBackToGeneralClickLis implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            DictActivity.this.finish();

        }

    }

    class IBDictPlayMusicByAccentClickLis implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub

            mp3Box.playMusicByWord(searchedWord,false, true);

        }

    }

    class IBDictAddWordToGlossaryClickLis implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            showAddDialog();
        }

    }

    class IBDictDeleteEditTextClickLis implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            editTextDictSearch.setText("");
        }

    }

    class BDictDialogConfirmClickLis implements View.OnClickListener {

        AlertDialog dialog=null;
        public BDictDialogConfirmClickLis(AlertDialog dialog){
            this.dialog=dialog;
        }
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            insertWordToGlossary();
            dialog.cancel();
        }

    }

    class BDictDialogCancelClickLis implements View.OnClickListener {
        AlertDialog dialog=null;
        public BDictDialogCancelClickLis(AlertDialog dialog){
            this.dialog=dialog;
        }

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            dialog.cancel();
        }

    }

    class EditTextDictEditActionLis implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
            // TODO Auto-generated method stub
            if(arg1== EditorInfo.IME_ACTION_SEARCH || arg2!=null&&arg2.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                startSearch();
                return true;
            }

            return false;
        }

    }


    class IBDictSearchClickLis implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            startSearch();

        }

    }

    public void startSearch(){

        String str=editTextDictSearch.getText().toString();
        if(str==null || str.equals(""))
            return;
        searchedWord=str;
        new ThreadDictSearchWordAndSetInterface().start();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextDictSearch.getWindowToken(), 0);

    }
}



