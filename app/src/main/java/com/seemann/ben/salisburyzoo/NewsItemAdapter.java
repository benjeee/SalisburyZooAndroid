package com.seemann.ben.salisburyzoo;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Ben on 1/23/2017.
 */

public class NewsItemAdapter extends BaseAdapter {

    private final MainActivity context;
    private NewsTabController newsTab;
    private ArrayList<NewsItem> newsItems;
    private StorageReference storageRef;
    private LayoutInflater inflater;
    private DisplayMetrics metrics;
    private final String LOG_TAG = "News Item Adapter";

    NewsItemAdapter(MainActivity context, ArrayList<NewsItem> newsItems, StorageReference storageRef, NewsTabController newsTab){
        this.context = context;
        this.newsItems = newsItems;
        this.storageRef = storageRef;
        this.newsTab = newsTab;
        this.inflater = context.getLayoutInflater();
        metrics = context.getResources().getDisplayMetrics();
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int i) {
        return newsItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //make list item model, etc
        View rowView= inflater.inflate(R.layout.news_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final NewsItem curr = newsItems.get(i);
        txtTitle.setText(curr.getName());
        txtTitle.setWidth(metrics.widthPixels);
        txtTitle.setHeight(metrics.widthPixels/5);
        if(i%2 == 0){
            txtTitle.setPadding(25,0,0,0);
            txtTitle.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        } else {
            txtTitle.setPadding(0,0,25,0);
            txtTitle.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        }
        int n = i%5;
        switch (n){
            case 0:
                txtTitle.setBackgroundResource(R.color.zooBlue);
                break;
            case 1:
                txtTitle.setBackgroundResource(R.color.zooPale);
                break;
            case 2:
                txtTitle.setBackgroundResource(R.color.zooGreen);
                break;
            case 3:
                txtTitle.setBackgroundResource(R.color.zooPurple);
                break;
            case 4:
                txtTitle.setBackgroundResource(R.color.zooBrown);
                break;
        }
        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "FRICKKK");
                newsTab.showNewsDetails(curr);
            }
        });
        return rowView;
    }
}
