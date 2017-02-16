package com.zhuyu.bysj;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zhuyu.bysj.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    private WebView webView;
    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* Bundle bundle=getArguments();
        String url=bundle.getString("result");
        Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();*/
        View view=inflater.inflate(R.layout.fragment_web_view, container, false);
        webView= (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        refreshWebView(MainActivity.result);

        return view;
    }
    public void refreshWebView(String url){
        if (url!=null){
            webView.loadUrl(url);
        }
    }

}
