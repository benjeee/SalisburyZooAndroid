package com.seemann.ben.salisburyzoo;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ben on 1/23/2017.
 */



public class NewsTabController {

    private TextView toolbarText;
    private MainActivity context;
    private ListView newsList;
    private ArrayList<NewsItem> newsItems;
    private NewsItemAdapter newsItemAdapter;
    private FirebaseDatabase db;
    private StorageReference storageRef;
    private final String LOG_TAG = "News Tab Controller";
    private LinearLayout newsDetails;
    private TextView newsDetailsText;
    private ImageView newsDetailsImg;
    private double width;
    private Button backButton;

    NewsTabController(MainActivity context, FirebaseDatabase db, StorageReference storageRef){
        this.context = context;
        this.db = db;
        this.storageRef = storageRef;
        this.toolbarText = (TextView) context.findViewById(R.id.toolbar_title);
        this.newsList = (ListView) context.findViewById(R.id.news_list);
        newsItems = new ArrayList();
        newsItemAdapter = new NewsItemAdapter(context, newsItems, storageRef, this);
        newsList.setAdapter(newsItemAdapter);
        newsList.setBackgroundColor(Color.WHITE);
        initNewsList();
        newsItemAdapter.notifyDataSetChanged();
        newsDetails = (LinearLayout) context.findViewById(R.id.news_details);
        newsDetailsText = (TextView) context.findViewById(R.id.news_details_txt);
        newsDetailsImg = (ImageView) context.findViewById(R.id.news_details_img);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        backButton = (Button) context.findViewById(R.id.back_button_news);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewsTab();
            }
        });
    }

    public void showNewsTab(){
        newsList.setVisibility(View.VISIBLE);
        toolbarText.setVisibility(View.VISIBLE);
        newsDetails.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        toolbarText.setText("News and Events");
    }
    public void hideNewsTab() {
        newsList.setVisibility(View.GONE);
        newsDetails.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
    }
    public void showNewsDetails(NewsItem curr){
        hideNewsTab();
        newsDetails.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        newsDetailsImg.setImageBitmap(null);
        toolbarText.setText(curr.getName());
        newsDetailsText.setText(Html.fromHtml(curr.getDetailsString()));
        if(curr.getImage() != null){
            storageRef.child(curr.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri.toString()).resize((int) width, (int) width).centerCrop().into(newsDetailsImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }else{
        }
    }

    public void initNewsList(){
        DatabaseReference myRef = db.getReference("News");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot newsSnapshot : dataSnapshot.getChildren()) {
                    String key = newsSnapshot.getKey();
                    final NewsItem newsItem = newsSnapshot.getValue(NewsItem.class);
                    newsItem.setName(key);
                    newsItems.add(newsItem);
                    Collections.sort(newsItems);
                    Log.d(LOG_TAG, key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

