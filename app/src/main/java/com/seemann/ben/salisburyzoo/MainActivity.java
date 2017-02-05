package com.seemann.ben.salisburyzoo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seemann.ben.salisburyzoo.barcode.BarcodeCaptureActivity;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int BARCODE_READER_REQUEST_CODE = 100;

    private BottomNavigationView bottomNavigationView;
    private TextView toolbarText;

    private StorageReference storageRef;
    private FirebaseDatabase db;

    private AnimalTabController animalTab;
    private NewsTabController newsTab;
    private WebTabController webTab;
    private InfoTabController infoTab;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public Typeface tf;

    private ImageView startup_img;
    private LinearLayout startup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        startup = (LinearLayout) findViewById(R.id.startup);
        startup_img = (ImageView) findViewById(R.id.startup_img);
        startup_img.getLayoutParams().height=metrics.heightPixels;
        startup_img.getLayoutParams().width=metrics.widthPixels;
        Picasso.with(this).load(R.drawable.logo).into(startup_img);
        startup.setBackgroundResource(R.color.white);
        startup.setVisibility(View.VISIBLE);
        startup.bringToFront();

        startup_img.postDelayed(new Runnable() {
            public void run() {
                startup.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                animalTab.showAnimalsTab();
            }
        }, 4000);


        mAuth = FirebaseAuth.getInstance();
        auth();

        db = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        animalTab = new AnimalTabController(this, db, storageRef);
        newsTab = new NewsTabController(this, db, storageRef);
        webTab = new WebTabController(this);
        infoTab = new InfoTabController(this);

        animalTab.addFlamingo();

        tf = Typeface.createFromAsset(getAssets(),
                "fonts/Twiddlestix.otf");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarText = (TextView) findViewById(R.id.toolbar_title);
        toolbarText.setTypeface(tf);
        animalTab.populateAnimalListFromDB();



        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        showTabClicked(item);
                        return false;

                    }
                });
    }
    private void showTabClicked(MenuItem item){
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.action_inventory:
                newsTab.hideNewsTab();
                webTab.hideWebTab();
                infoTab.hide();
                animalTab.showAnimalsTab();
                break;
            case R.id.action_news:
                animalTab.hideAnimalsTab();
                webTab.hideWebTab();
                infoTab.hide();
                newsTab.showNewsTab();
                break;
            case R.id.action_web:
                animalTab.hideAnimalsTab();
                newsTab.hideNewsTab();
                infoTab.hide();
                webTab.showWebTab();
                break;
            case R.id.action_info:
                animalTab.hideAnimalsTab();
                newsTab.hideNewsTab();
                infoTab.show();
                webTab.hideWebTab();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    animalTab.showObtained();
                    bottomNavigationView.setVisibility(View.GONE);
                    animalTab.hideAnimalsTab();
                    newsTab.hideNewsTab();
                    webTab.hideWebTab();
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    animalTab.scanExhibit(barcode.displayValue);
                    animalTab.notifyAdapter();
                } else System.out.println(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void auth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(LOG_TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w(LOG_TAG, "signInAnonymously", task.getException());
                    }
                }
            });
    }



}