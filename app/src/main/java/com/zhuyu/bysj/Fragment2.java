package com.zhuyu.bysj;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {


    private static Fragment2 fragment2;
    private Fragment2() {
        // Required empty public constructor
    }
    public static Fragment2 getInstance(){
        if (fragment2==null){
            fragment2=new Fragment2();
        }
        return fragment2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment2, container, false);
    }

}
