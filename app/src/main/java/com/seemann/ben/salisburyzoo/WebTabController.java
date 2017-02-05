package com.seemann.ben.salisburyzoo;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Ben on 1/23/2017.
 */

public class WebTabController {

    private WebView website;
    private TextView toolbarText;
    private MainActivity context;
    Button refresh;

    WebTabController(MainActivity context){
        this.context = context;

        website = (WebView) context.findViewById(R.id.web_page);

        MyBrowser myBrowser = new MyBrowser();
        website.setWebViewClient(myBrowser);
        website.getSettings().setLoadsImagesAutomatically(true);
        website.getSettings().setJavaScriptEnabled(true);
        website.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        website.loadUrl("http://mobile.salisburyzoo.org/");
        refresh = (Button) context.findViewById(R.id.web_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                website.loadUrl("http://mobile.salisburyzoo.org/");
            }
        });
        this.toolbarText = (TextView) context.findViewById(R.id.toolbar_title);
    }

    public void showWebTab(){
        website.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        toolbarText.setVisibility(View.VISIBLE);
        toolbarText.setText("Zoo Website");
    }
    public void hideWebTab(){
        refresh.setVisibility(View.GONE);
        website.setVisibility(View.GONE);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
