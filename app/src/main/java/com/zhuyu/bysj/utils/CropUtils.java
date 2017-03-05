package com.zhuyu.bysj.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.zhuyu.bysj.R;

import java.io.File;

/**
 * Created by ZHUYU on 2017/2/21 0021.
 */

public class CropUtils {

    /**
     * 启动裁剪
     * @param activity 上下文

     * @param aspectRatioX 裁剪图片宽高比
     * @param aspectRatioY 裁剪图片宽高比
     * @return
     */
    public static void startUCrop(Activity activity, Uri sourceUri,
                                   Uri destinationUri, float aspectRatioX, float aspectRatioY) {

        //初始化，第一个参数：需要裁剪的图片；第二个参数：裁剪后图片
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        //初始化UCrop配置
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        //options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //是否隐藏底部容器，默认显示
        //options.setHideBottomControls(true);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(activity, R.color.colorPrimaryDark));
        //是否能调整裁剪框
        //options.setFreeStyleCropEnabled(true);
        //UCrop配置
        uCrop.withOptions(options);
        //设置裁剪图片的宽高比，比如16：9
        uCrop.withAspectRatio(aspectRatioX, aspectRatioY);
        //uCrop.withMaxResultSize(900,900);
        //uCrop.useSourceImageAspectRatio();
        //跳转裁剪页面
        uCrop.start(activity);
    }


}
