package com.zhuyu.bysj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.Names;
import com.zhuyu.bysj.utils.RequestSaveUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlterActivity extends AppCompatActivity {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.lenText)
    TextView lenText;
    @BindView(R.id.doneBtn)
    FloatingActionButton doneBtn;

    /**
     * @param context 上下文
     * @param name    文本输入类型根据Names文件中的字符串来判断
     * @param value    值
     */
    public static void actionstart(Context context, String name, String value, int id) {
        Intent intent = new Intent(context, AlterActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("value", value);
        intent.putExtra("id", id);
        ((AppCompatActivity) context).startActivityForResult(intent,ALTERACTIVITY_CODE);
    }
    public static final int ALTERACTIVITY_CODE=1;
    private String name;
    private int length;//文本最大长度
    private int lenofinfo;//当前输入的文本长度
    private String title;//界面标题
    private int id;    //id，可以是userid或是goodsid，根据type来判断
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        ButterKnife.bind(this);
        initview();
    }


    private void initview() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        value = intent.getStringExtra("value");
        id = intent.getIntExtra("id", -1);
        Log.d("AlterActivity", "value: "+value);
        if (TextUtils.isEmpty(value))
            editText.setText("");
        else {
            editText.setText(value);
            editText.setSelection(value.length());//将光标移至文字末尾
        }
        switch (name) {
            case Names.PHONE:
                title = "修改手机号";
                length = 11;
                editText.setKeyListener(new DigitsKeyListener(false, false));
                break;
            case Names.IDNUMBER:
                title = "修改身份证号";
                editText.setKeyListener(new DigitsKeyListener(false, false));
                length = 18;
                break;
            case Names.USERNAME:
                title = "更改昵称";
                length = 20;
                break;
            case Names.WORDS:
                title = "个性签名";
                length = 30;
                break;
            case Names.BUY_ADDRESS:
                title = "购买地址";
                length = 30;
                break;
            case Names.BUY_PRICE:
                title = "购买价格";
                length = 11;
                editText.setKeyListener(new DigitsKeyListener(false, false));
                break;
        }

        ActionBarUtil.create(this, title, ActionBarUtil.DEFAULT_HOME);
        lenofinfo = editText.getText().length();
        lenText.setText(lenofinfo + "/" + length);//设置长度下标
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editinfo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save:
                save();
                break;
            case R.id.undo:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnTextChanged(R.id.editText)
    public void OnTextChanged() {
        lenofinfo = editText.getText().length();
        lenText.setText(lenofinfo + "/" + length);
        if (lenofinfo > length) {
            lenText.setTextColor(ActivityCompat.getColor(this, R.color.colorPrimary));

        } else {
            lenText.setTextColor(ActivityCompat.getColor(this, R.color.textColor));
        }
    }

    @OnClick(R.id.doneBtn)
    public void onClick() {
        save();
    }

    //保存并提交修改后的数据
    private void save() {
         value = editText.getText().toString();//获得内容
        if (lenofinfo > length) {
            editText.setError("超出字数限制！");
            return;
        }
        if (TextUtils.isEmpty(value.trim())) {
            editText.setError("内容不能为空！");
            return;
        }

        switch (name) {
            case Names.PHONE:
                if (value.length() != 11) {
                    editText.setError("请输入正确的手机号！");
                    return;
                }
                RequestSaveUtil.requestSaveUserInfo(this, Names.PHONE, Names.PHONE, value, id);
                break;
            case Names.IDNUMBER:
                if (value.length() != 18) {
                    editText.setError("请输入正确的身份证号码！");
                    return;
                }
                RequestSaveUtil.requestSaveUserInfo(this, Names.IDNUMBER, Names.IDNUMBER, value, id);
                break;
            case Names.USERNAME:
                RequestSaveUtil.requestSaveUserInfo(this, Names.USERNAME, Names.USERNAME, value, id);
                break;
            case Names.WORDS:
                RequestSaveUtil.requestSaveUserInfo(this, Names.WORDS, Names.WORDS, value, id);
                break;
            case Names.BUY_ADDRESS:
                RequestSaveUtil.requestSaveGoodsInfo(this, Names.BUY_ADDRESS, Names.BUY_ADDRESS, value, id,callback);
                break;
            case Names.BUY_PRICE:
                RequestSaveUtil.requestSaveGoodsInfo(this, Names.BUY_PRICE, Names.BUY_PRICE, value, id,callback);
                break;
        }
       
        
    }
    Callback callback=new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AlterActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String result = response.body().string();

            if (result.equals("ok")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlterActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("name", name);
                        intent.putExtra("value", value);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            } else if (result.equals("error")) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlterActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    };
}
