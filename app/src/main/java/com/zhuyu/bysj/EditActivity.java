package com.zhuyu.bysj;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.zhuyu.bysj.R.id.dateText;
import static com.zhuyu.bysj.R.id.stateBtn;


public class EditActivity extends AppCompatActivity {

    private ImageView  addressImage, priceImage;
    private EditText addressText, priceText;
    private FloatingActionButton saveBtn;
    private TextView goodsNameText;
    private Button dateBtn, stateBtn;
    private StateDialog stateDialog;
    private String stateCode;
    OkHttpClient client=new OkHttpClient();
    private Goods goods;
    private Type type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
        initEvent();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("编辑商品信息");

        addressImage = (ImageView) findViewById(R.id.addressImage);
        priceImage = (ImageView) findViewById(R.id.priceImage);

        addressText = (EditText) findViewById(R.id.addressText);
        priceText = (EditText) findViewById(R.id.priceText);
        saveBtn = (FloatingActionButton) findViewById(R.id.saveBtn);
        goodsNameText = (TextView) findViewById(R.id.goodsNameText);

        dateBtn = (Button) findViewById(R.id.dateBtn);
        stateBtn = (Button) findViewById(R.id.stateBtn);

         goods = (Goods) getIntent().getExtras().getSerializable("goods");
         type = (Type) getIntent().getExtras().getSerializable("type");
        Log.d("editActivity", "goods: " + goods.toString());
        dateBtn.setText(goods.getBuy_date());
        addressText.setText(goods.getBuy_address());
        priceText.setText(goods.getBuy_price());
        goodsNameText.setText(type.getTrademark_chinese() + type.getTypename());

        showState();//根据goods中的state状态  修改statebtn和statecode内容

    }
    private void showState(){
        switch (goods.getState()){
            case "normal":stateBtn.setText("正常");stateCode="normal";break;
            case "lost":stateBtn.setText("已挂失");stateCode="lost";break;
            case "unregistered":stateBtn.setText("已注销");stateCode="unregistered";break;
            default:break;
        }
    }
    private void initEvent() {


        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setstate();
            }
        });
    }

    private void save() {
        String goodsid=goods.getId()+"";
        String buydate=dateBtn.getText().toString();
        String buyaddress=addressText.getText().toString();
        String buyprice=priceText.getText().toString();

        String url=MainActivity.baseUrl+"saveGoodsInfo";
        FormBody formBody=new FormBody.Builder()
                .add("state",stateCode)
                .add("buydate",buydate)
                .add("buyaddress",buyaddress)
                .add("buyprice",buyprice)
                .add("goodsid",goodsid)
                .build();
        Request request=new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(EditActivity.this, "上传失败，请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.equals("ok")){
                            Toast.makeText(EditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }else if(result.equals("error")){
                            Toast.makeText(EditActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    private void setstate() {
        if (stateDialog == null) {
            stateDialog = new StateDialog(this);
            stateDialog.setOnStateClickListener(new StateDialog.OnStateClickListener() {
                @Override
                public void onNormalClick() {
                    stateBtn.setText("正常");
                    stateCode = "normal";
                    stateDialog.dismiss();
                }

                @Override
                public void onLostClick() {
                    stateBtn.setText("已挂失");
                    stateCode = "lost";
                    stateDialog.dismiss();
                }

                @Override
                public void onUnbindClick() {
                    stateBtn.setText("已注销");
                    stateCode = "unregistered";
                    stateDialog.dismiss();
                }
            });
        }
        stateDialog.show();
    }

    private void setDate() {
        //初始化日历信息
        Calendar calendar = Calendar.getInstance();
        //获取当前年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("setDate", year + "-" + month + "-" + day);
        /**context：当前上下文；
         callback：OnDateSetListener日期改变监听器；
         year：初始化的年；
         monthOfYear：初始化的月（从0开始计数，所以实际应用时需要加1）；
         dayOfMonth：初始化的日；
         */
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateBtn.setText(year + "年" + (month + 1) + "月" + dayOfMonth + "日");

            }
        }, year, month, day);
        datePickerDialog.show();
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
