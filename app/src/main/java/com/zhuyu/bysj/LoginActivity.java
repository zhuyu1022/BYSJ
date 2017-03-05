package com.zhuyu.bysj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Goodsinfo;
import com.zhuyu.bysj.bean.GoodsAndType;
import com.zhuyu.bysj.bean.LoginInfo;
import com.zhuyu.bysj.bean.Type;
import com.zhuyu.bysj.bean.User;
import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.GlideCacheUtil;
import com.zhuyu.bysj.utils.Names;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneText, passwordText;
    private Button loginBtn, registerBtn;
    private OkHttpClient client;
    private static final int REGISTER_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //创建数据库
        LitePal.getDatabase();
        initView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String phone = preferences.getString(Names.PHONE, null);
        String password = preferences.getString(Names.PASSWORD, null);
        if ((!TextUtils.isEmpty(phone)) && (!TextUtils.isEmpty(password))) {
            phoneText.setText(phone);
            passwordText.setText(password);
        }

    }

    private void initView() {
        ActionBarUtil.create(this, "欢迎", ActionBarUtil.NO_HOME);
        phoneText = (EditText) findViewById(R.id.accountText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        client = new OkHttpClient();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                login();
                break;
            case R.id.registerBtn:
                register();
                break;
            default:
                break;
        }
    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_CODE);
    }


    private void login() {
        loginBtn.setText("登陆中...");
        String phone = phoneText.getText().toString();
        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            phoneText.setError("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            passwordText.setError("密码不能为空");
            return;
        }

        loginRequest(phone, password);
    }

    private void loginRequest(final String phone, final String password) {
        FormBody formBody = new FormBody.Builder()
                .add(Names.PHONE, phone)
                .add(Names.PASSWORD, password)
                .build();
        String url = MainActivity.baseUrl + "login";
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();

                        loginBtn.setText("登录");

                    }
                });
                Log.d("onFailure: ", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d("response:", result);
                try {
                    Gson gson = new Gson();
                    LoginInfo loginInfo = gson.fromJson(result, LoginInfo.class);
                    Log.d("loginInfo", loginInfo.toString());

                    String state = loginInfo.state;
                    Log.d("state", state);
                    if (state.equals("ok")) {
                        User user = loginInfo.user;
                        //保存用户信息到本地
                        saveUser(user);
                        //1、清空数据库，包括goods表和type表
                        DataSupport.deleteAll(Goods.class);
                        DataSupport.deleteAll(Type.class);
                        //2、清空glide中的所有缓存
                        GlideCacheUtil.getInstance().clearImageAllCache(LoginActivity.this);

                       Goodsinfo goodsinfo=loginInfo.goodsinfo;
                        int counts=goodsinfo.counts;
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                        editor.putInt(Names.COUNTS, counts);    //保存商品列表数量，用于后面的fragmentadaptet适配器判断用户是否有商品，动态添加fragment
                        editor.apply();
                        if (counts>0){
                          List<GoodsAndType> goodsAndTypelist =  goodsinfo.goodsAndTypelist;
                            for (int i = 0; i <goodsAndTypelist.size() ; i++) {
                                GoodsAndType goodsAndType=goodsAndTypelist.get(i);
                                Goods goods=goodsAndType.goods;
                                Type type=goodsAndType.type;
                                //如果当前goods在数据库中不存在就保存，防止多余数据产生
                                if (DataSupport.where("goodsid=?",goods.getGoodsid()+"").find(Goods.class).size()==0)
                                { goods.save();}
                                if (DataSupport.where("typeid=?",type.getTypeid()+"").find(Type.class).size()==0)
                                { type.save();}
                            }
                        }
                        //跳转主界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // intent.putExtra("goodsAndTypelist", (Serializable) goodsAndTypelist);
                        startActivity(intent);
                        finish();
                    } else if (state.equals("none")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "账号不存在！", Toast.LENGTH_SHORT).show();
                                phoneText.setError("账号不存在！");
                            }
                        });
                    } else if (state.equals("error")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                passwordText.setError("密码错误！");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginBtn.setText("登录");
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REGISTER_CODE:
                if (resultCode == RESULT_OK) {
                    String phone = data.getExtras().getString(Names.PHONE);
                    String password = data.getExtras().getString(Names.PASSWORD);
                    // Log.d("phone", phone);
                    //Log.d("password", password);
                    if ((!TextUtils.isEmpty(phone)) && (!TextUtils.isEmpty(password))) {
                        phoneText.setText(phone);
                        passwordText.setText(password);
                        loginRequest(phone, password);
                    }
                }
                break;
            default:
                break;
        }
    }


    private void saveUser(User user) {
        //将用户信息保存到本地
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        editor.putString(Names.PHONE, user.getPhone());     //用户账号，也是手机号
        editor.putString(Names.PASSWORD, user.getPassword());     //  用户密码
        editor.putInt(Names.USERID, user.getUserid());          //用户id
        editor.putString(Names.USERNAME, user.getUsername());   //用户昵称
        editor.putString(Names.IDNUMBER, user.getIdnumber());   //身份证
        editor.putString(Names.SEX, user.getSex());
        editor.putInt(Names.SCORE, user.getScore());
        editor.putString(Names.WORDS, user.getWords());
        editor.putString(Names.ICON, user.getIcon());
        editor.apply();
    }
}
