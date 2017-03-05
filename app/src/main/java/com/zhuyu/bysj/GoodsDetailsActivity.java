package com.zhuyu.bysj;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;
import com.zhuyu.bysj.dialog.MyAlertDialog;
import com.zhuyu.bysj.utils.ActionBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsDetailsActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.trademarkText)
    TextView trademarkText;
    @BindView(R.id.typeNameText)
    TextView typeNameText;
    @BindView(R.id.priceText)
    TextView priceText;
    @BindView(R.id.goodsidText)
    TextView goodsidText;
    @BindView(R.id.dateText)
    TextView dateText;
    @BindView(R.id.stateText)
    TextView stateText;
    @BindView(R.id.queryTimesText)
    TextView queryTimesText;
    @BindView(R.id.realnameText)
    TextView realnameText;
    @BindView(R.id.phoneText)
    TextView phoneText;
    @BindView(R.id.buydateText)
    TextView buydateText;
    @BindView(R.id.buyaddressText)
    TextView buyaddressText;
    @BindView(R.id.buypriceText)
    TextView buypriceText;
    @BindView(R.id.callLayout)
    LinearLayout callLayout;
    @BindView(R.id.moreinfoCardView)
    CardView moreinfoCardView;
    @BindView(R.id.editBtn)
    FloatingActionButton editBtn;
    @BindView(R.id.buypictureImage)
    ImageView buypictureImage;


    private OkHttpClient client = new OkHttpClient();
    private Goods goods = null;
    private Type type = null;
    private String url;
    private int userid;
    /*
        如果是摄像头扫描跳转到此界面的话，url应该是http://192.168.1.102:8080/BYSJ/queryGoods?type=scan&goodsid=10000002
        如果是用户点击跳转到此界面的话 ，url应该是http://192.168.1.102:8080/BYSJ/queryGoods?type=click&goodsid=10000002
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        initView();
        url = MainActivity.baseUrl + getIntent().getStringExtra("url");//获得url；
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userid = preferences.getInt("userid", -1);//获取本地的用户id
        if (userid != -1) {
            queryGoods(url, userid);//执行查询操作
        }
    }

    private void initView() {
        ActionBarUtil.create(this, "商品详情", ActionBarUtil.DEFAULT_HOME);
        moreinfoCardView.setVisibility(View.GONE);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(phoneText.getText().toString());
            }
        });


    }

    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*
    跳转商品信息编辑界面
     */
    private void edit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int useridOfThisGoods = goods.getUserid();
        String state = goods.getState();
        if (userid != -1) {
            //先判断该商品是否可注册
            if (state.equals("new") || state.equals("unbind")) {
                MyAlertDialog.showAlertDialogWithAction(GoodsDetailsActivity.this, "提示", "该商品需要绑定用户信息后才能进行更多操作，是否立刻绑定？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GoodsDetailsActivity.this, BindGoodsActivity.class);
                        intent.putExtra("goods", goods);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }
                });
            }

            //再判断当前商品是否属于当前用户
            else if (userid == useridOfThisGoods) {
                //跳转
                Intent intent = new Intent(GoodsDetailsActivity.this, EditGoodsActivity.class);
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
    private void queryGoods(final String url, final int userid) {
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
                        Toast.makeText(GoodsDetailsActivity.this, "查询失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("result", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    final String state = jsonObject.getString("state");
                    Log.d("state", state);
                    if (state.equals("none")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoodsDetailsActivity.this, "商品不存在或已被官方删除", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        JSONObject jsonObjectGoods = jsonObject.getJSONObject("goods");
                        JSONObject jsonObjectType = jsonObject.getJSONObject("type");
                        final String realname = jsonObject.getString("realname");
                        final String phone = jsonObject.getString("phone");
                        Log.d("realname", realname);
                        Log.d("phone", phone);
                        Gson gson = new Gson();
                        goods = gson.fromJson(jsonObjectGoods.toString(), Goods.class);//获得商品信息
                        type = gson.fromJson(jsonObjectType.toString(), Type.class);//获得商品型号信息
                        final String goodsState = goods.getState();
                        Log.d("goods", goods.toString());
                        Log.d("type", type.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showGoodsInfo(goods, type);

                                if (url.contains("scan")&&goodsState.equals("new")) {
                                    if (goods.getQuerytimes()<6){
                                        MyAlertDialog.showAlertDialog(GoodsDetailsActivity.this, "恭喜", "该商品为全新正品");
                                    }else {
                                        MyAlertDialog.showAlertDialog(GoodsDetailsActivity.this, "提示", "该商品扫描次数过多，谨防假冒伪劣产品！");
                                    }

                                }
                                //只要商品不是全新未注册的就显示更多商品信息
                                if (!goodsState.equals("new")) {

                                    showMoreInfo(goods, realname, phone);
                                }
                                if (state.equals("no")) {
                                    if (goods.getState().equals("lost")) {
                                        MyAlertDialog.showAlertDialog(GoodsDetailsActivity.this, "警告", "该商品已挂失，请尽快联系失主！");
                                    }
                                }


                            }
                        });
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
        goodsidText.setText(goods.getGoodsid() + "");
        dateText.setText(goods.getDate());

        showState();
        //  stateText.setText(goods.getState());
        queryTimesText.setText(goods.getQuerytimes() + "");

        String url = MainActivity.baseUrl + type.getPicture();
        Glide.with(this).load(url).into(imageView);
    }

    /**
     * 显示商品的更多信息，只在商品注册之后
     *
     * @param goods
     */
    private void showMoreInfo(Goods goods, String realname, String phone) {

        moreinfoCardView.setVisibility(View.VISIBLE);

        realnameText.setText(realname);
        phoneText.setText(phone);
        buydateText.setText(goods.getBuy_date());
        buyaddressText.setText(goods.getBuy_address());
        buypriceText.setText(goods.getBuy_price());
       Log.d("picture", MainActivity.baseUrl+goods.getBuy_picture());
        Glide.with(this)
                .load(MainActivity.baseUrl+goods.getBuy_picture())
                .into(buypictureImage);

    }

    private void showState() {
        switch (goods.getState()) {
            case "normal":
                stateText.setText("正常");
                break;
            case "lost":
                stateText.setText("已挂失");
                break;
            case "unbind":
                stateText.setText("已解绑");
                break;
            case "new":
                if (goods.getQuerytimes() > 5) {
                    stateText.setText("商品扫描次数过多，谨防假冒伪劣产品！");
                } else {
                    stateText.setText("全新正品");
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        String url=MainActivity.baseUrl+"queryGoods?type=click&goodsid="+goods.getGoodsid();
        queryGoods(url, userid);
        super.onRestart();
    }
}
