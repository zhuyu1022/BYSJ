package com.zhuyu.bysj;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuyu.bysj.bean.Goods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private TextView trademarkText, typeNameText, priceText, goodsidText, dateText, stateText, queryTimesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        String url = getIntent().getStringExtra("url");//获得二维码中的url；
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int userid = preferences.getInt("userid", -1);//获取本地的用户id
        if (userid != -1) {
            queryGoods(url, userid);//执行查询操作
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        trademarkText = (TextView) findViewById(R.id.trademarkText);
        typeNameText = (TextView) findViewById(R.id.typeNameText);
        priceText = (TextView) findViewById(R.id.priceText);
        goodsidText = (TextView) findViewById(R.id.goodsidText);
        dateText = (TextView) findViewById(R.id.dateText);
        stateText = (TextView) findViewById(R.id.stateText);
        queryTimesText = (TextView) findViewById(R.id.queryTimesText);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("商品详情");
    }

    private void queryGoods(String url, int userid) {
        FormBody formBody = new FormBody.Builder()
                .add("userid", userid + "")
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
                        Toast.makeText(DetailsActivity.this, "查询失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("state");
                    Log.d("state", state);
               /*     if (state.equals("none")) {

                    }
                    //商品属于当前用户
                    else {*/


                        JSONArray jsonArray=jsonObject.getJSONArray("goods");
                        jsonObject=jsonArray.getJSONObject(0);
                        Gson gson=new Gson();
                        final Goods goods=gson.fromJson(jsonObject.toString(),Goods.class);
                        Log.d("goods", goods.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showGoodsInfo(goods);
                            }
                        });
               /*         if (state.equals("yes")) {


                        }
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showGoodsInfo(Goods goods) {
        goodsidText.setText(goods.getId()+"");
        dateText.setText(goods.getDate());
        stateText.setText(goods.getState());
        queryTimesText.setText(goods.getQuerytimes()+"");
    }


}
