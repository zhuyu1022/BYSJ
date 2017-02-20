package com.zhuyu.bysj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import com.zhuyu.bysj.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText accountText, passwordText;
    private Button loginBtn, registerBtn;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String account = preferences.getString("account", null);
        String password = preferences.getString("password", null);
        if ((!TextUtils.isEmpty(account)) && (!TextUtils.isEmpty(password))) {
            accountText.setText(account);
            passwordText.setText(password);
        }

    }

    private void initView() {
        accountText = (EditText) findViewById(R.id.accountText);
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
        startActivity(intent);
    }


    private void login() {
        loginBtn.setText("登陆中...");
        String account = accountText.getText().toString();
        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            accountText.setError("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            passwordText.setError("密码不能为空");
            return;
        }

        loginRequest(account, password);
    }

    private void loginRequest(final String account, final String password) {
        FormBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("password", password)
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

                    }
                });
                Log.d("onFailure: ", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d("response:", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("state");
                    Log.d("state", state);
                    if (state.equals("ok")) {
                        //Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                        //解析返回的json数据
                        JSONArray jsonArray = jsonObject.getJSONArray("user");
                        jsonObject = jsonArray.getJSONObject(0);
                        Gson gson = new Gson();
                        User user = gson.fromJson(jsonObject.toString(), User.class);
                        //将用户信息保存到本地
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                        editor.putString("account", user.getAccount());
                        editor.putString("password", user.getPassword());
                        editor.putInt("userid", user.getId());//用户id
                        editor.putString("phone", user.getPhone());
                        editor.putString("realname", user.getRealname());
                        editor.apply();
                        //跳转主界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (state.equals("none")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "账号不存在！", Toast.LENGTH_SHORT).show();
                                accountText.setError("账号不存在！");
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }   finally {
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
}
