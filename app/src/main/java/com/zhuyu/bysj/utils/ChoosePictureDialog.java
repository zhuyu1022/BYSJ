package com.zhuyu.bysj.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhuyu.bysj.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ZHUYU on 2017/2/20 0020.
 */

public class ChoosePictureDialog extends Dialog {
    @BindView(R.id.takephotoBtn)
    Button takephotoBtn;
    @BindView(R.id.formalbumBtn)
    Button formalbumBtn;

    private OnDialogClickListener onDialogClickListener;
    public ChoosePictureDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choosepicture);
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
    }

    @OnClick({R.id.takephotoBtn, R.id.formalbumBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takephotoBtn:
                onDialogClickListener.onTakephotoClick();

                break;
            case R.id.formalbumBtn:
                onDialogClickListener.onFromalbumClick();
                break;
        }
    }
    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener){
        this.onDialogClickListener=onDialogClickListener;
    }

    public interface OnDialogClickListener{
        void onTakephotoClick();
        void onFromalbumClick();
    }


}
