package com.zhuyu.bysj.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by ZHUYU on 2017/2/16 0016.
 */


public class Type extends DataSupport implements Serializable {
    private int id;
    private int typeid;
    private String typename;
    private String trademark;
    private String price;
    private String picture;
    private String trademark_chinese;
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

    public String getTypename() {
        return typename;
    }
    public void setTypename(String typename) {
        this.typename = typename;
    }
    public String getTrademark() {
        return trademark;
    }
    public void setTrademark(String trademark) {
        this.trademark = trademark;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getTrademark_chinese() {
        return trademark_chinese;
    }
    public void setTrademark_chinese(String trademark_chinese) {
        this.trademark_chinese = trademark_chinese;
    }

    @Override
    public String toString() {
        return "Type{" +
                "trademark_chinese='" + trademark_chinese + '\'' +
                ", picture='" + picture + '\'' +
                ", price='" + price + '\'' +
                ", trademark='" + trademark + '\'' +
                ", typename='" + typename + '\'' +
                ", typeid=" + typeid +
                '}';
    }
}
