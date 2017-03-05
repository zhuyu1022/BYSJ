package com.zhuyu.bysj.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zhuyu.bysj.MainActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZHUYU on 2017/2/27 0027.
 */

public class RequestSaveUtil {
    /**
     * @param name   需要保存的对象名称
     * @param value  保存的值
     * @param userid 用户id
     */
    public static void requestSaveUserInfo(final Context context, String flag, final String name, final String value, final int userid) {
        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.baseUrl + "alterUserInfo";
        FormBody formBody = new FormBody.Builder()
                .add("flag", flag)
                .add(name, value)
                .add(Names.USERID, userid + "")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                if (result.equals("ok")) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

                    editor.putString(name, value);//保存数据
                    editor.apply();
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (result.equals("error")) {
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public static void requestSaveGoodsInfo(final Context context, String flag, final String name, final String value, final int goodsid,Callback callback) {

        OkHttpClient client = new OkHttpClient();
        String url = MainActivity.baseUrl + "alterGoodsInfo";
        FormBody formBody = new FormBody.Builder()
                .add("flag", flag)
                .add(name, value)
                .add(Names.GOODSID, goodsid + "")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
