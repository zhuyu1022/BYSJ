package com.zhuyu.bysj.bean;

import java.util.List;

/**
 * Created by ZHUYU on 2017/2/25 0025.
 */

public class Goodsinfo {

    public int counts;
    public List<GoodsAndType> goodsAndTypelist;

    @Override
    public String toString() {
        return "Goodsinfo{" +
                "counts=" + counts +
                ", goodsAndTypelist=" + goodsAndTypelist +
                '}';

    }
}
