package com.zhuyu.bysj.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by ZHUYU on 2017/2/25 0025.
 */

public class GoodsAndType extends DataSupport implements Serializable {

        public  Goods goods;
        public Type type;

        @Override
        public String toString() {
            return "GoodsAndType{" +
                    "goods=" + goods +
                    ", type=" + type +
                    '}';
        }
    }

