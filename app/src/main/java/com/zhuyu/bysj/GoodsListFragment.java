package com.zhuyu.bysj;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.GoodsAndType;
import com.zhuyu.bysj.bean.Goodsinfo;
import com.zhuyu.bysj.bean.LoginInfo;
import com.zhuyu.bysj.bean.Type;
import com.zhuyu.bysj.bean.User;
import com.zhuyu.bysj.utils.GlideCacheUtil;
import com.zhuyu.bysj.utils.GoodsAdapter;
import com.zhuyu.bysj.utils.Names;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoodsListFragment extends Fragment {

    private static GoodsListFragment goodsListFragment;

    private GoodsListFragment() {
        // Required empty public constructor
    }

    public static GoodsListFragment getInstance() {
        if (goodsListFragment == null) {
            goodsListFragment = new GoodsListFragment();
        }
        return goodsListFragment;
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OkHttpClient okHttpClient;

    private AppCompatActivity mContext;
   private List<Goods> goodsList;
    //private List<GoodsAndType> goodsAndTypeList;
    GoodsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods, container, false);
        initView(view);
        initDates();

        return view;

    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initDates() {
        mContext = (AppCompatActivity) getContext();
        goodsList = DataSupport.findAll(Goods.class);
        adapter = new GoodsAdapter(mContext, goodsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void refresh() {

        //1、重新请求服务器获取数据,如果获取成功在进行其他的步骤
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String phone = preferences.getString(Names.PHONE, null);
        String password = preferences.getString(Names.PASSWORD, null);
        if ((!TextUtils.isEmpty(phone)) && (!TextUtils.isEmpty(password))) {
            goodlistRequest(phone, password);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void goodlistRequest(final String phone, final String password) {
        FormBody formBody = new FormBody.Builder()
                .add(Names.PHONE, phone)
                .add(Names.PASSWORD, password)
                .build();
        String url = MainActivity.baseUrl + "login";
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d("response:", result);
                try {
                    Gson gson = new Gson();
                    LoginInfo loginInfo = gson.fromJson(result, LoginInfo.class);
                    Log.d("loginInfo", loginInfo.toString());
                    String state = loginInfo.state;
                    Log.d("state", state);
                    if (state.equals("ok")) {
                        //1、清空数据库，包括goods表和type表
                        DataSupport.deleteAll(Goods.class);
                        DataSupport.deleteAll(Type.class);
                        //2、清空glide中的所有缓存
                        GlideCacheUtil.getInstance().clearImageAllCache(mContext);

                        Goodsinfo goodsinfo = loginInfo.goodsinfo;
                        int counts = goodsinfo.counts;
                        if (counts > 0) {
                            List<GoodsAndType> goodsAndTypelist = goodsinfo.goodsAndTypelist;
                            final List<Goods> mgoodsList = new ArrayList<Goods>();
                            for (int i = 0; i < goodsAndTypelist.size(); i++) {
                                GoodsAndType goodsAndType = goodsAndTypelist.get(i);
                                Goods goods = goodsAndType.goods;
                                Type type = goodsAndType.type;
                                //3、保存数据，如果当前goods在数据库中不存在就保存，防止多余数据产生
                                if (DataSupport.where("goodsid=?", goods.getGoodsid() + "").find(Goods.class).size() == 0) {
                                    goods.save();
                                }
                                if (DataSupport.where("typeid=?", type.getTypeid() + "").find(Type.class).size() == 0) {
                                    type.save();
                                }
                                mgoodsList.add(goods);
                            }
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //3、清空集合goodsList
                                    goodsList.clear();
                                    goodsList .addAll(mgoodsList);
                                   GoodsAdapter adapter=new GoodsAdapter(mContext,goodsList);
                                 recyclerView.setAdapter(adapter);
                                  adapter.notifyDataSetChanged();//刷新数据
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });

    }
}
