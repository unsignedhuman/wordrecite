package com.example.wordrecite.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DictSentenceListAdapter extends BaseAdapter{

    private Context context=null;
    private int resources;
    private ArrayList<HashMap<String,Object>> list=null;
    private String[] from;
    private int[] to;


    /**
     * 这里仿照的是SimpleAdapter的形参列表
     * @param context
     * @param resources
     * @param list
     * @param from
     * @param to
     */

    public DictSentenceListAdapter(Context context, int resources,
                                   ArrayList<HashMap<String, Object>> list, String[] from, int[] to) {
        super();
        this.context = context;
        this.resources = resources;
        this.list = list;
        this.from = from;
        this.to = to;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        LayoutInflater inflater=LayoutInflater.from(context);
        contentView=inflater.inflate(resources, null);
        TextView text=(TextView)contentView.findViewById(to[0]);
        text.setText((String)(list.get(position).get(from[0])));
        return contentView;
    }

}
