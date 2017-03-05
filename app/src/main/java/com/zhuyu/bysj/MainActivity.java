package com.zhuyu.bysj;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.zhuyu.bysj.bean.LoginInfo;
import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.Names;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    public final static String baseUrl = "http://192.168.1.100:8080/BYSJ/";
    // public final static String baseUrl = "http://16jl687129.51mypc.cn:37749/BYSJ/";
    public static String result = null;
    private final static int SCAN_CODE = 1;
    private final static int SCAN_PERMISSON = 1;
    private final static int SETTING_CODE = 2;

    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.scanBtn)
    FloatingActionButton scanBtn;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.outloginLayout)
    LinearLayout outloginLayout;
    @BindView(R.id.closeLayout)
    LinearLayout closeLayout;
    private OkHttpClient client = new OkHttpClient();
    private TextView nav_accountText;
    private String account = null;
    private TextView resultText;
    private CircleImageView navIcon;
    private String username;
    private String icon;
    private int userid;
    File userIcon;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        initView();

    }

    private void initView() {

        ActionBarUtil.createWithResId(this, "IGMS", R.drawable.ic_menu);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);


        View navHeaderView = navigationView.getHeaderView(0);//获取侧滑菜单头部布局
        nav_accountText = (TextView) navHeaderView.findViewById(R.id.nav_accountText);//获取头部布局中的控件
        navIcon = (CircleImageView) navHeaderView.findViewById(R.id.nav_icon);
        navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPersonalInfo();
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(Names.USERNAME, null);
        icon = preferences.getString(Names.ICON, null);
        userid = preferences.getInt(Names.USERID, -1);


        Log.d("MainActivity", userid + "");
        if (userid != -1) {
            userIcon = new File(getExternalCacheDir(), userid + "usericon.jpg");
        }


        //检查本地存储的文件是否存在
        if (userIcon.exists()) {
            Log.d("userIcon", "exists");
            long time = userIcon.lastModified();//获得文件最后的修改时间
            Glide.with(this)
                    .load(userIcon)
                    .signature(new StringSignature(time + ""))    //增加签名
                    .into(navIcon);
        } else {
            Log.d("userIcon", "not exists");
            //如果不存在就加载网络图片
            String iconUrl = MainActivity.baseUrl + icon;
            Glide.with(this).load(iconUrl)
                    .error(R.drawable.user)
                    .into(navIcon);
        }
        if (!TextUtils.isEmpty(username)) {
            nav_accountText.setText(username);
        }
    }


    private void setPersonalInfo() {
        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    private void scan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, SCAN_PERMISSON);
        } else {
            showProgressDialog();
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, SCAN_CODE);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SCAN_PERMISSON:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan();
                } else {
                    Toast.makeText(this, "你拒绝了权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCAN_CODE:
                closeProgressDialog();
                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    result = bundle.getString("result");

                    if (result.contains("queryGoods?type=")) {
                        Intent intent = new Intent(MainActivity.this, GoodsDetailsActivity.class);
                        intent.putExtra("url", result);
                        startActivity(intent);
                    } else if (result.contains("http")) {
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);
                        intent.putExtra("url", result);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "暂不支持此格式", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            case SETTING_CODE:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        //检查本地存储的文件是否存在
        if (userIcon.exists()) {
            Log.d("userIcon", "exists");
            long time = userIcon.lastModified();//获得文件最后的修改时间
            Glide.with(this)
                    .load(userIcon)
                    .signature(new StringSignature(time + ""))    //增加签名
                    .into(navIcon);
        } else {
            Log.d("userIcon", "not exists");
            String iconUrl = MainActivity.baseUrl + icon;
            //如果不存在就加载网络图片
            Glide.with(this).load(iconUrl)
                    .error(R.drawable.user)
                    .into(navIcon);
        }

        super.onRestart();
    }


    @OnClick({R.id.scanBtn, R.id.outloginLayout, R.id.closeLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scanBtn:
                scan();
                break;
            case R.id.outloginLayout:
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.closeLayout:
                this.finish();
                break;
        }
    }
private void showProgressDialog(){
    if (progressDialog==null){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    progressDialog.show();
}
private void closeProgressDialog(){
    if (progressDialog!=null){
        progressDialog.dismiss();
    }
}
}
