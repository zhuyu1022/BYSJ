package com.zhuyu.bysj.bean;

/**
 * Created by ZHUYU on 2017/2/15 0015.
 */

public class User {
    private int id;
    private String account;
    private String password;
    private String phone;
    public User() {
    }

    public User( String account, String password,String phone) {

        this.account = account;
        this.password = password;
        this.phone=phone;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
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

    @Override
    public String toString() {
        return "User [id=" + id + ", account=" + account + ", password=" + password + "]";
    }


}
