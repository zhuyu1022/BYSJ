package com.zhuyu.bysj;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ZHUYU on 2017/2/20 0020.
 */

public class CustomTitle extends AppBarLayout {
    // 命名空间，在引用这个自定义组件的时候，需要用到
    private String namespace = "http://schemas.android.com/apk/res/com.zhuyu.bysj.CustomTitle";
    private AppCompatActivity activity;
    private Button backBtn;
    private TextView titleText;
    String title=null;
    public CustomTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        activity = (AppCompatActivity) context;
        title=attrs.getAttributeValue(3);
        //title=attrs.getAttributeValue(namespace,"title");
       // Log.d("title", title);
        //initView();
        //initEvent();
    }

    private void initEvent() {
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }


    private void initView() {

       // backBtn= (Button) findViewById(R.id.backBtn);
        //titleText= (TextView) findViewById(R.id.titleText);
    }


}
