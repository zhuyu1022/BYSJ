package com.zhuyu.bysj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.zhuyu.bysj.R;


/**
 * Created by ZHUYU on 2017/2/19 0019.
 */

public class StateDialog extends Dialog {
    private LinearLayout normalLayout, lostLayout, unbindLayout;

    private OnStateClickListener onStateClickListener;

    public StateDialog(Context context) {
        super(context);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_statedialog);
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        // initData();
        //初始化界面控件的事件
        initEvent();
    }

    private void initView() {
        normalLayout = (LinearLayout) findViewById(R.id.normalLayout);
        lostLayout = (LinearLayout) findViewById(R.id.lostLayout);
        unbindLayout = (LinearLayout) findViewById(R.id.unbindLayout);
    }

    private void initEvent() {
        normalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStateClickListener.onNormalClick();
            }
        });
        lostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStateClickListener.onLostClick();
            }
        });
        unbindLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStateClickListener.onUnbindClick();
            }
        });
        normalLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void setOnStateClickListener(OnStateClickListener onStateClickListener) {
        this.onStateClickListener = onStateClickListener;
    }


    /**
     * 设置监听器，面向接口的编程
     */
    public interface OnStateClickListener {
        void onNormalClick();

        void onLostClick();

        void onUnbindClick();

    }
}
