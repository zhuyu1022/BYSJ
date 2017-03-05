package com.zhuyu.bysj.bean;

import java.util.List;

/**
 * Created by ZHUYU on 2017/2/25 0025.
 */
//对应登陆之后json实体类
public class LoginInfo {
    public String state;
    public User user;
    public Goodsinfo goodsinfo;

    @Override
    public String toString() {
        return "LoginInfo{" +
                "state='" + state + '\'' +
                ", user=" + user +
                ", goodsinfo=" + goodsinfo +
                '}';
    }
}

