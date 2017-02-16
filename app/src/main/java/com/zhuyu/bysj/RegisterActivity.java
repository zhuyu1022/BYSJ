package com.zhuyu.bysj;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText accountText, passwordText, confirmPasswordText, phoneText;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        client = new OkHttpClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新用户注册");
        accountText = (EditText) findViewById(R.id.accountText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {
        String account = accountText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPaaword = confirmPasswordText.getText().toString();
        String phone = phoneText.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入账号！", Toast.LENGTH_SHORT).show();
            accountText.setError("请输入账号！");
            return;
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPaaword)) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
            passwordText.setError("请输入密码!");
            confirmPasswordText.setError("请输入密码!");
            return;
        } else if (!password.equals(confirmPaaword)) {
            Toast.makeText(this, "两次密码输入不一致，重新输入！", Toast.LENGTH_SHORT).show();
            passwordText.setError("密码不一致!");
            confirmPasswordText.setError("密码不一致!");
            return;
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号!", Toast.LENGTH_SHORT).show();
            phoneText.setError("请输入手机号!");
            return;
        }

        if (password.length() < 6) {
            passwordText.setError("密码不能小于6位");
            return;
        } else if (phone.length()!=11){
            phoneText.setError("请输入正确的手机号码！");
            Toast.makeText(this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
        }


        String url = MainActivity.baseUrl + "register";
        FormBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("password", password)
                .add("phone",phone)
                .build();
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
                        Toast.makeText(RegisterActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.equals("ok")) {
                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        } else if (result.equals("error")) {
                            Toast.makeText(RegisterActivity.this, "账号已存在！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
