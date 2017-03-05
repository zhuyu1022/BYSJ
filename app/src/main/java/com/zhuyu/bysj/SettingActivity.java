package com.zhuyu.bysj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhuyu.bysj.utils.ActionBarUtil;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
    }

    private void initview() {
        ActionBarUtil.create(this,"设置",ActionBarUtil.DEFAULT_HOME);
    }

}
