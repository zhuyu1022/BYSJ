package com.zhuyu.bysj;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;
import com.zhuyu.bysj.dialog.ChoosePictureDialog;
import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.CropUtils;
import com.zhuyu.bysj.utils.Names;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private final static int TAKEPHOTO_CODE = 1;
    private final static int TAKEPHOTO_PERMISSION = 1;
    private final static int FROMALBUM_CODE = 2;
    private final static int FROMALBUM_PERMISSION = 2;
    private Uri usericonUri;
    private File userIcon;
    private int userid;
    private String username;
    private String phone;
    private String idnumber;
    private int score;
    private String sex;
    private String area;
    private String words;
    private String icon;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        ActionBarUtil.create(this, "个人信息", ActionBarUtil.DEFAULT_HOME);

        client = new OkHttpClient();
        showinfo();
    }

    private void showinfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userid = preferences.getInt(Names.USERID, -1);
        icon = preferences.getString(Names.ICON, null);
        username = preferences.getString(Names.USERNAME, null);
        phone = preferences.getString(Names.PHONE, null);
        idnumber = preferences.getString(Names.IDNUMBER, null);
        score = preferences.getInt(Names.SCORE, -1);
        sex = preferences.getString(Names.SEX, null);
        area = preferences.getString(Names.AREA, null);
        words = preferences.getString(Names.WORDS, null);
        //创建file对象，用于存储拍照后的照片,采用应用缓存位置，就不用申请权限了
        userIcon = new File(getExternalCacheDir(), userid + "usericon.jpg");


        if (userIcon.exists()) {
            long time = userIcon.lastModified();//获得文件最后的修改时间
            Glide.with(this)
                    .load(userIcon)
                    .signature(new StringSignature(time + ""))   // 重点在这行
                    .into(iconImage);
        } else {
            //如果本地没有图片缓存，再从网络加载
            String iconUrl = MainActivity.baseUrl + icon;
            Glide.with(this).load(iconUrl).error(R.drawable.user).into(iconImage);
        }

        if (TextUtils.isEmpty(username)) usernameText.setText("未填写");
        else usernameText.setText(username);

        if (TextUtils.isEmpty(phone)) phoneText.setText("未填写");
        else phoneText.setText(phone);

        if (TextUtils.isEmpty(idnumber)) idnumberText.setText("未填写");
        else idnumberText.setText(idnumber);

        if (score == -1) scoreText.setText("未填写");
        else scoreText.setText(score + "");

        if (TextUtils.isEmpty(sex)) sexText.setText("未填写");
        else sexText.setText(sex);

        if (TextUtils.isEmpty(area)) areaText.setText("未填写");
        else areaText.setText(area);

        if (TextUtils.isEmpty(words)) wordsText.setText("未填写");
        else wordsText.setText(words);
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
                changeIcon();//更换头像
                break;
            case R.id.usernameLayout:
                AlterActivity.actionstart(this, Names.USERNAME, username, userid);
                break;
            case R.id.phoneLayout:
                AlterActivity.actionstart(this, Names.PHONE, phone, userid);
                break;
            case R.id.idnumberLayout:
                AlterActivity.actionstart(this, Names.IDNUMBER, idnumber, userid);
                break;
            case R.id.scoreLayout:
                break;
            case R.id.sexLayout:
                break;
            case R.id.areaLayout:
                break;
            case R.id.wordsLayout:
                AlterActivity.actionstart(this, Names.WORDS, words, userid);
                break;
        }
    }

    private void changeIcon() {
        //检查权限并请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKEPHOTO_PERMISSION);
        } else {
            if (dialog == null) {
                dialog = new ChoosePictureDialog(this);
                dialog.setOnDialogClickListener(new ChoosePictureDialog.OnDialogClickListener() {
                    @Override
                    public void onTakephotoClick() {
                        takephoto();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFromalbumClick() {
                        fromalbum();
                        dialog.dismiss();
                    }
                });
            }
            dialog.show();
        }
    }

    /**
     * 拍照
     */
    private void takephoto() {
        try {
            if (userIcon.exists()) {
                userIcon.delete();
            }
            userIcon.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解决7.0系统相机崩溃问题
        if (Build.VERSION.SDK_INT < 24) {
            usericonUri = Uri.fromFile(userIcon);
        } else {
            usericonUri = FileProvider.getUriForFile(this, "com.zhuyu.bysj", userIcon);
        }
      Log.d("usericonUri",usericonUri+"");
        // 启动相机程序
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, usericonUri);//为相机应用指定图片的输出地址
        startActivityForResult(intent, TAKEPHOTO_CODE);

    }

    /**
     * 从相册选择
     */
    private void fromalbum() {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, FROMALBUM_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKEPHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    Log.d("personalActivity", "usericonUri:" + usericonUri);
                    //需要将usericonUri转换为fileuri
                    //所以
                    Uri uri=Uri.fromFile(userIcon);

                    //进行裁剪,裁剪比例1：1
                    CropUtils.startUCrop(this, uri, uri, 1, 1);
                }
                break;
            case FROMALBUM_CODE:
                if (resultCode == RESULT_OK) {
                    //获得图片真是路径
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
                    c.close();
                    //根据相册中图片路径获取uri，作为ucrop的sourceUri
                    Uri iconFromAlbumUri = Uri.fromFile(new File(imagePath));
                    //目标Uri
                    Uri uri=Uri.fromFile(userIcon);
                    //进行裁剪,裁剪比例1：1
                    CropUtils.startUCrop(this, iconFromAlbumUri, uri, 1, 1);
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {

                    uploadIcon(userIcon, userid);//上传头像
                }
                break;

        }
    }

    /**
     * 上传图片
     *
     * @param file
     * @param userid
     */
    private void uploadIcon(final File file, final int userid) {

        Log.d("userid", userid + "");
        String url = MainActivity.baseUrl + "uploadIcon";
        RequestBody filebBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Names.USERID, userid + "")
                .addFormDataPart("image", userid + ".jpg", filebBody)
                .build();

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("IOException", e.toString());
                        Toast.makeText(PersonalActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d("result", result);
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("state");
                    if (state.equals("ok")) {
                        String icon = jsonObject.getString("icon");
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PersonalActivity.this).edit();
                        editor.putString(Names.ICON, icon);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                                long time = file.lastModified();//获得文件最后的修改时间
                                //记载图片
                                Glide.with(PersonalActivity.this)
                                        .load(userIcon)
                                        .signature(new StringSignature(time + ""))   // 重点在这行
                                        .into(iconImage);
                            }
                        });

                    } else if (state.equals("error")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case TAKEPHOTO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    changeIcon();
                else
                    Toast.makeText(this, "你拒绝了权限！", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    @Override
    protected void onRestart() {
        Log.d("PersonalActivity:", "onRestart: ");
        showinfo();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
