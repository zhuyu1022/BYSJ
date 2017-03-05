package com.zhuyu.bysj;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {
    private static EmptyFragment emptyFragment;
    private EmptyFragment() {
        // Required empty public constructor
    }

    public static EmptyFragment getInstance() {
        if (emptyFragment == null) {
            emptyFragment = new EmptyFragment();
        }
        return emptyFragment;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

}
