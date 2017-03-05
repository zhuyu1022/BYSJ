package com.zhuyu.bysj;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhuyu.bysj.utils.Names;

/**
 * Created by ZHUYU on 2017/2/24 0024.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private  static final int PAGE_COUNT=3;
    private Context mContext;
    public MyFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext=context;
    }

    /**
     * 返回每个page显示的Fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               /* SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
                int counts=preferences.getInt(Names.COUNTS,-1);
                if (counts>0){*/
                    return GoodsListFragment.getInstance();
               /* }else {
                    return EmptyFragment.getInstance();
                }*/


            case 1:
                return  Fragment2.getInstance();

            case 2:
                return Fragment3.getInstance();

            default:
                return GoodsListFragment.getInstance();
        }
    }

    /**
     * 返回page的数量
     * @return
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
     * 作为顶部标签的显示内容
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "广场";

            case 1:
                return "好友";

            case 2:
                return "我";

            default:
                return "微博";
        }
    }
}
