package com.zhuyu.bysj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;

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

    private TextView realnameText, phoneText, buydateText, buyaddressText, buypriceText;
    FloatingActionButton editBtn;
    private ImageView imageView;
    private Goods goods = null;
    private Type type = null;
    private CardView  moreinfoCardView;

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
        imageView = (ImageView) findViewById(R.id.imageView);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        trademarkText = (TextView) findViewById(R.id.trademarkText);
        typeNameText = (TextView) findViewById(R.id.typeNameText);
        priceText = (TextView) findViewById(R.id.priceText);
        goodsidText = (TextView) findViewById(R.id.goodsidText);
        dateText = (TextView) findViewById(R.id.dateText);
        stateText = (TextView) findViewById(R.id.stateText);
        queryTimesText = (TextView) findViewById(R.id.queryTimesText);
        editBtn = (FloatingActionButton) findViewById(R.id.editBtn);

        realnameText = (TextView) findViewById(R.id.realnameText);
        phoneText = (TextView) findViewById(R.id.phoneText);
        buydateText = (TextView) findViewById(R.id.buydateText);
        buyaddressText = (TextView) findViewById(R.id.buyaddressText);
        buypriceText = (TextView) findViewById(R.id.buypriceText);

        moreinfoCardView = (CardView) findViewById(R.id.moreinfoCardView);
        moreinfoCardView.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("商品详情");
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

    }

    /*
    跳转商品信息编辑界面
     */
    private void edit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int userid = preferences.getInt("userid", -1);
        int useridOfGoods = goods.getUserid();
        if (userid != -1) {
            //先判断当前商品是否属于当前用户
            if (userid == useridOfGoods) {
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("goods", goods);
                intent.putExtra("type", type);
                startActivity(intent);
            } else {
                Toast.makeText(this, "你不是该商品主人，无权进行该操作", Toast.LENGTH_SHORT).show();
            }
        }


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

    /**
     * 查询商品信息
     *
     * @param url
     * @param userid
     */
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
                    if (state.equals("none")) {
                        Toast.makeText(DetailsActivity.this, "商品不存在或已被官方删除", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObjectGoods = jsonObject.getJSONObject("goods");
                        JSONObject jsonObjectType = jsonObject.getJSONObject("type");
                        final String realname=jsonObject.getString("realname");
                        final String phone=jsonObject.getString("phone");
                        Log.d("realname",realname);
                        Log.d("phone",phone);
                        Gson gson = new Gson();
                        goods = gson.fromJson(jsonObjectGoods.toString(), Goods.class);//获得商品信息
                        type = gson.fromJson(jsonObjectType.toString(), Type.class);//获得商品型号信息
                        String goodsState=goods.getState();
                        Log.d("goods", goods.toString());
                        Log.d("type", type.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showGoodsInfo(goods, type);
                            }
                        });
                        //只要商品不是全新未注册的就显示更多商品信息
                        if (!goodsState.equals("new")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showMoreInfo(goods,realname,phone);
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 显示商品基本信息
     *
     * @param goods
     * @param type
     */
    private void showGoodsInfo(Goods goods, Type type) {
        //Log.d("showGoodsInfo", "type: "+type.toString());
        trademarkText.setText(type.getTrademark_chinese() + "/" + type.getTrademark());
        typeNameText.setText(type.getTypename());
        priceText.setText(type.getPrice());
        goodsidText.setText(goods.getId() + "");
        dateText.setText(goods.getDate());
        stateText.setText(goods.getState());
        queryTimesText.setText(goods.getQuerytimes() + "");

        String url = MainActivity.baseUrl + type.getPicture();
        Glide.with(this).load(url).into(imageView);
    }

    /**
     * 显示商品的更多信息，只在商品注册之后
     *
     * @param goods
     */
    private void showMoreInfo(Goods goods,String realname,String phone) {

        moreinfoCardView.setVisibility(View.VISIBLE);
        realnameText.setText(realname);
        phoneText.setText(phone);
        buydateText.setText(goods.getBuy_date());
        buyaddressText.setText(goods.getBuy_address());
        buypriceText.setText(goods.getBuy_price());

    }


}
