package com.zhuyu.bysj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuyu.bysj.utils.ChoosePictureDialog;

import java.io.BufferedInputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalActivity extends AppCompatActivity {


    @BindView(R.id.iconImage)
    ImageView iconImage;
    @BindView(R.id.iconLayout)
    LinearLayout iconLayout;
    @BindView(R.id.usernameText)
    TextView usernameText;
    @BindView(R.id.usernameLayout)
    LinearLayout usernameLayout;
    @BindView(R.id.phoneText)
    TextView phoneText;
    @BindView(R.id.phoneLayout)
    LinearLayout phoneLayout;
    @BindView(R.id.idnumberText)
    TextView idnumberText;
    @BindView(R.id.idnumberLayout)
    LinearLayout idnumberLayout;
    @BindView(R.id.scoreText)
    TextView scoreText;
    @BindView(R.id.scoreLayout)
    LinearLayout scoreLayout;
    @BindView(R.id.sexText)
    TextView sexText;
    @BindView(R.id.sexLayout)
    LinearLayout sexLayout;
    @BindView(R.id.areaText)
    TextView areaText;
    @BindView(R.id.areaLayout)
    LinearLayout areaLayout;
    @BindView(R.id.wordsText)
    TextView wordsText;
    @BindView(R.id.wordsLayout)
    LinearLayout wordsLayout;
    @BindView(R.id.activity_personal)
    CoordinatorLayout activityPersonal;

    private ChoosePictureDialog dialog = null;
    private final static int TAKEPHOTO_CODE=1;
    private final static int TAKEPHOTO_PEIMISSION=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("个人信息");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iconLayout, R.id.usernameLayout, R.id.phoneLayout, R.id.idnumberLayout, R.id.scoreLayout, R.id.sexLayout, R.id.areaLayout, R.id.wordsLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iconLayout:
                    uploadIcon();//上传头像
                break;
            case R.id.usernameLayout:
                break;
            case R.id.phoneLayout:
                break;
            case R.id.idnumberLayout:
                break;
            case R.id.scoreLayout:
                break;
            case R.id.sexLayout:
                break;
            case R.id.areaLayout:
                break;
            case R.id.wordsLayout:
                break;
        }
    }

    private void uploadIcon(){
        if (dialog == null) {
            dialog = new ChoosePictureDialog(this);
            dialog.setOnDialogClickListener(new ChoosePictureDialog.OnDialogClickListener() {
                @Override
                public void onTakephotoClick() {
                    takephoto();
                }
                @Override
                public void onFromalbumClick() {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    private void takephoto(){
        if (ContextCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PersonalActivity.this,new String[]{Manifest.permission.CAMERA},TAKEPHOTO_PEIMISSION);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKEPHOTO_CODE);
            dialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKEPHOTO_CODE:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case TAKEPHOTO_PEIMISSION:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    takephoto();
                else
                    Toast.makeText(this, "你拒绝了权限！", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
