package com.zhuyu.bysj;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Fragment3 extends Fragment {


    private static Fragment3 fragment3;
    private Fragment3() {
        // Required empty public constructor
    }
    public static Fragment3 getInstance(){
        if (fragment3==null){
            fragment3=new Fragment3();
        }
        return fragment3;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment3, container, false);
    }


}
