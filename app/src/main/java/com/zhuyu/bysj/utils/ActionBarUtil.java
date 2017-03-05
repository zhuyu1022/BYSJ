package com.zhuyu.bysj.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.zhuyu.bysj.R;

/**
 * Created by ZHUYU on 2017/2/21 0021.
 */

public class ActionBarUtil {
    public static final String NO_HOME="1";
    public static final String  DEFAULT_HOME="2";
    static AppCompatActivity activity;
    public static void create(Context context,String title,String  type){
        activity= (AppCompatActivity) context;
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(title);
        switch (type){
            case NO_HOME:
                actionBar.setDisplayHomeAsUpEnabled(false);
                break;
            case DEFAULT_HOME:
                actionBar.setDisplayHomeAsUpEnabled(true);
                break;
            default:
                break;
        }


    }
    public static void createWithResId(Context context,String title,int resourceId){
        activity= (AppCompatActivity) context;
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);

        actionBar.setHomeAsUpIndicator(resourceId);

    }

}
