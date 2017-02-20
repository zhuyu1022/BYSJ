package com.zhuyu.bysj;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.zhuyu.bysj.utils.FragmentUtil;

import java.lang.reflect.Field;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    // public final static String baseUrl="http://10.0.2.2:8080/BYSJ/";
    public final static String baseUrl = "http://192.168.1.101:8080/BYSJ/";
    public static String result=null;
    private final static int SCAN_CODE=1;
    private final static int SCAN_PERMISSON=1;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private OkHttpClient client = new OkHttpClient();
    private TextView nav_accountText;
    private String account = null;
    private FloatingActionButton scanBtn;
    private TextView resultText;
    private FrameLayout fragmentLayout;
    private CircleImageView navIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("IGMS");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        View navHeaderView = navigationView.getHeaderView(0);//获取侧滑菜单头部布局
        nav_accountText = (TextView) navHeaderView.findViewById(R.id.nav_accountText);//获取头部布局中的控件
        navIcon= (CircleImageView) navHeaderView.findViewById(R.id.nav_icon);

        navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPersonalInfo();
            }
        });
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String realname=preferences.getString("realname",null);
        if (!TextUtils.isEmpty(realname)) {
            nav_accountText.setText(realname);
        }

        scanBtn= (FloatingActionButton) findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            scan();
        }
    });
        fragmentLayout= (FrameLayout) findViewById(R.id.fragmentLayout);
    }


    private void setPersonalInfo(){
        Intent intent=new Intent(MainActivity.this,PersonalActivity.class);
        startActivity(intent);
    }
    private void scan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},SCAN_PERMISSON);
        }else {
            Intent intent=new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent,SCAN_CODE);
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
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    scan();
                }else {
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
        switch (requestCode){
            case SCAN_CODE:
                if (resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    result = bundle.getString("result");

                    if (result.contains(baseUrl)){
                        Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
                        intent.putExtra("url",result);
                        startActivity(intent);
                    }else if (result.contains("http")){
                        Intent intent=new Intent(MainActivity.this,WebActivity.class);
                        intent.putExtra("url",result);
                        startActivity(intent);
                    }else {
                        Toast.makeText(this, "暂不支持此格式", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:break;
        }
    }


}
