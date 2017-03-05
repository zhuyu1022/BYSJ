package com.zhuyu.bysj.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by ZHUYU on 2017/2/15 0015.
 */

public class User extends DataSupport implements Serializable {
    private int id;
    private int userid;
    private String password;
    private String phone;
    private String username;
    private String idnumber;
    private String icon;
    private int score;
    private String sex;
    private String area;
    private String words;
    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", idnumber='" + idnumber + '\'' +
                ", icon='" + icon + '\'' +
                ", score=" + score +
                ", sex='" + sex + '\'' +
                ", area='" + area + '\'' +
                ", words='" + words + '\'' +
                '}';
    }
}
