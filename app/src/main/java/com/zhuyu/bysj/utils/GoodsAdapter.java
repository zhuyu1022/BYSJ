package com.zhuyu.bysj.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhuyu.bysj.GoodsDetailsActivity;
import com.zhuyu.bysj.MainActivity;
import com.zhuyu.bysj.R;
import com.zhuyu.bysj.bean.Goods;
import com.zhuyu.bysj.bean.Type;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZHUYU on 2017/2/24 0024.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    List<Goods> goodsList;
    List<Type> typelist;
    Context mContext;
    public GoodsAdapter(Context context,List<Goods> goodsList){
        this.goodsList=goodsList;
        mContext=context;
        typelist=new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            int typeid=goodsList.get(i).getTypeid();
            Type type= DataSupport.where ("typeid=?",typeid+"").find(Type.class).get(0);
            typelist.add(type);
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleText;
        private TextView priceText;
        private TextView dateText;
        private TextView querytimesText;
        private ImageView image;
        private ImageView stateImage;
        private TextView stateText;
        public ViewHolder(final View itemView) {
            super(itemView);
            titleText= (TextView) itemView.findViewById(R.id.item_TitleText);
            image= (ImageView) itemView.findViewById(R.id.item_Image);
            priceText= (TextView) itemView.findViewById(R.id.item_priceText);
            dateText= (TextView) itemView.findViewById(R.id.item_dateText);
            querytimesText= (TextView) itemView.findViewById(R.id.item_querytimesText);
            stateImage= (ImageView) itemView.findViewById(R.id.item_stateImage);
            stateText= (TextView) itemView.findViewById(R.id.item_stateText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    int goodsid=goodsList.get(position).getGoodsid();
                    String url=MainActivity.baseUrl+"queryGoods?type=click&goodsid="+goodsid;
                    Intent intent=new Intent(mContext, GoodsDetailsActivity.class);
                    intent.putExtra("url",url);
                    mContext.startActivity(intent);
                }
            });
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods,parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            Goods goods=goodsList.get(position);
        Type type=typelist.get(position);
        holder.titleText.setText(type.getTrademark()+"  "+type.getTypename());
        String url= MainActivity.baseUrl+type.getPicture();
        Glide.with(mContext)
                .load(url)
                .error(R.drawable.error)
                .into(holder.image);
        holder.priceText.setText(type.getPrice());
        holder.dateText.setText(goods.getDate()+"出厂");
        holder.querytimesText.setText("已扫描"+goods.getQuerytimes()+"次");
        switch (goods.getState()){
            case "normal":
                holder.stateText.setText("正常");
                holder.stateImage.setImageResource(R.drawable.normal);
                ColorStateList cs1=mContext.getResources().getColorStateList(R.color.state_color_green);
                holder.stateImage.setImageTintList(cs1);
                break;
            case "lost":
                holder.stateText.setText("已挂失");
                holder.stateImage.setImageResource(R.drawable.lost);
             ColorStateList cs2=mContext.getResources().getColorStateList(R.color.state_color_red);
                holder.stateImage.setImageTintList(cs2);
                break;
            case "unbind":
                holder.stateText.setText("已解绑");
                holder.stateImage.setImageResource(R.drawable.unbind);

                ColorStateList cs3=mContext.getResources().getColorStateList(R.color.state_color_blue);
                holder.stateImage.setImageTintList(cs3);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }


}
