package com.zhuyu.bysj.bean;

/**
 * Created by ZHUYU on 2017/2/16 0016.
 */
public class Goods {
    private int id;
    private int typeid;
    private int userid;
    private String date;
    private int querytimes;
    private String state;
    private String buy_date;
    private String buy_address;

    private String buy_price;
    private String buy_picture;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTypeid() {
        return typeid;
    }
    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getQuerytimes() {
        return querytimes;
    }
    public void setQuerytimes(int querytimes) {
        this.querytimes = querytimes;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getBuy_date() {
        return buy_date;
    }
    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }
    public String getBuy_address() {
        return buy_address;
    }
    public void setBuy_address(String buy_address) {
        this.buy_address = buy_address;
    }

    public String getBuy_price() {
        return buy_price;
    }
    public void setBuy_price(String buy_price) {
        this.buy_price = buy_price;
    }
    public String getBuy_picture() {
        return buy_picture;
    }
    public void setBuy_picture(String buy_picture) {
        this.buy_picture = buy_picture;
    }
    @Override
    public String toString() {
        return "Goods [id=" + id + ", typeid=" + typeid + ", userid=" + userid + ", date=" + date + ", querytimes="
                + querytimes + ", state=" + state + ", buy_date=" + buy_date + ", buy_address=" + buy_address
                + ", buy_price=" + buy_price + ", buy_picture=" + buy_picture + "]";
    }


}

