package com.zhuyu.bysj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;
import com.zhuyu.bysj.utils.ActionBarUtil;
import com.zhuyu.bysj.utils.Names;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BindGoodsActivity extends AppCompatActivity {

    @BindView(R.id.userImage)
    ImageView userImage;
    @BindView(R.id.usernameText)
    TextView usernameText;
    @BindView(R.id.goodsImage)
    ImageView goodsImage;
    @BindView(R.id.goodsNameText)
    TextView goodsNameText;
    @BindView(R.id.bindBtn)
    Button bindBtn;

    private int userid;
    private int goodsid;
    private Goods goods;
OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_goods);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        ActionBarUtil.create(this, "商品绑定", ActionBarUtil.DEFAULT_HOME);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String icon = preferences.getString(Names.ICON, null);
        String username = preferences.getString(Names.USERNAME, null);
        userid = preferences.getInt(Names.USERID, -1);
        String iconUrl = MainActivity.baseUrl + icon;

        Glide.with(this)
                .load(iconUrl)
                .error(R.drawable.user)
                .into(userImage);

        if (username != null) {
            usernameText.setText(username);
        }
        goods = (Goods) getIntent().getSerializableExtra("goods");
        Type type = (Type) getIntent().getSerializableExtra("type");
        goodsid=goods.getGoodsid();
        goodsNameText.setText(type.getTypename());
        String pictureUrl = MainActivity.baseUrl + type.getPicture();
        Glide.with(this)
                .load(pictureUrl)
                .error(R.drawable.error)
                .into(goodsImage);
    }

    @OnClick(R.id.bindBtn)
    public void onClick() {
        requestBindGoods(userid,goodsid);

    }


    private void requestBindGoods(int userid,int goodsid){
        if (client==null){
            client=new OkHttpClient();
        }
        String url=MainActivity.baseUrl+"bindGoods";

        FormBody formBody=new FormBody.Builder()
                .add(Names.USERID,userid+"")
                .add(Names.GOODSID,goodsid+"")
                .build();
        Request request=new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BindGoodsActivity.this, "绑定失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                if (result.equals("ok")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BindGoodsActivity.this, "绑定成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if (result.equals("error")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BindGoodsActivity.this, "绑定失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
