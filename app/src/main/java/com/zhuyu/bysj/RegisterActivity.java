package com.zhuyu.bysj;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.Names;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.phoneText)
    EditText phoneText;
    @BindView(R.id.passwordText)
    EditText passwordText;
    @BindView(R.id.confirmPasswordText)
    EditText confirmPasswordText;
    @BindView(R.id.usernameText)
    EditText usernameText;
    @BindView(R.id.maleBtn)
    RadioButton maleBtn;
    @BindView(R.id.femaleBtn)
    RadioButton femaleBtn;
    @BindView(R.id.registerBtn)
    Button registerBtn;
    @BindView(R.id.sexBtn)
    RadioGroup sexBtn;
    private OkHttpClient client;
    private  String sex="男";
    private String phone ;
    private String password ;
    private String confirmPaaword ;
    private String username ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        client = new OkHttpClient();
        ActionBarUtil.create(this, "新用户注册", ActionBarUtil.DEFAULT_HOME);
        sexBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==maleBtn.getId()){
                    sex="男";
                }else if (checkedId==femaleBtn.getId()){
                    sex="女";
                }
            }
        });
    }
    @OnClick(R.id.registerBtn)
    public void onClick() {
        register();
    }
    private void register() {
        phone = phoneText.getText().toString();
        password = passwordText.getText().toString();
        confirmPaaword = confirmPasswordText.getText().toString();
        username = usernameText.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneText.setError("请输入手机号！");
            return;
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPaaword)) {
            passwordText.setError("请输入密码!");
            confirmPasswordText.setError("请输入密码!");
            return;
        } else if (!password.equals(confirmPaaword)) {
            passwordText.setError("密码不一致!");
            confirmPasswordText.setError("密码不一致!");
            return;
        }  else if (password.length() < 6) {
            passwordText.setError("密码不能小于6位");
            return;
        } else if (phone.length() != 11) {
            phoneText.setError("请输入正确的手机号码！");
            return;
        } else if (username.length()>20){
            usernameText.setError("昵称过长！");
            return;
        }
        requestRegister(phone,password,username,sex);
    }
    private void requestRegister(String phone,String password,String username,String sex){
        String url = MainActivity.baseUrl + "register";
        FormBody formBody = new FormBody.Builder()
                .add(Names.PHONE, phone)
                .add(Names.PASSWORD, password)
                .add(Names.USERNAME,username)
                .add(Names.SEX,sex)
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

                            showFinishDialog();
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
    private void showFinishDialog(){
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setMessage("您已注册成功!")
                .setTitle("恭喜")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.putExtra(Names.PHONE,phone);
                        intent.putExtra(Names.PASSWORD,password);
                        setResult(RESULT_OK,intent);
                     finish();
                    }
                })
                .show();
    }


}
