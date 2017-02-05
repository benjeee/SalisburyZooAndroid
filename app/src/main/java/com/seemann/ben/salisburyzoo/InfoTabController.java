package com.seemann.ben.salisburyzoo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

/**
 * Created by Ben on 1/25/2017.
 */

public class InfoTabController {
    private LinearLayout infoLayout;
    private ImageView infoImg;
    private TextView infoTxt;
    private TextView toolbarText;
    private TextView link;
    private Button fb;
    InfoTabController(final MainActivity context){
        infoLayout = (LinearLayout) context.findViewById(R.id.info_zoo_layout);
        infoImg = (ImageView) context.findViewById(R.id.info_zoo_img);
        infoTxt = (TextView) context.findViewById(R.id.info_zoo_txt);
        toolbarText = (TextView) context.findViewById(R.id.toolbar_title);
        link = (TextView) context.findViewById(R.id.info_zoo_link);
        fb = (Button) context.findViewById(R.id.fb);
        final String url = "https://www.facebook.com/pages/Salisbury-Zoo/109389752419844";
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager pm = context.getPackageManager();
                    context.startActivity(newFacebookIntent(pm, url));
                } catch(Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });
        link.setText(
                Html.fromHtml(
                        "<a href=\"http://www.salisburyzoo.org/\">Salisbury Zoo Website</a><br> "));
        link.setMovementMethod(LinkMovementMethod.getInstance());
        infoImg.setImageResource(R.drawable.logo);
        infoTxt.setText(
                "Open 9am - 4:30pm every day of the year except Thanksgiving and Christmas.\n\n" +
                "Location: 755 South Park Drive, Salisbury MD 21804\n\n" +
                "FREE ADMISSION AND PARKING!!\n\n"+
                "For more information, visit our website and facebook pages:\n"
        );
        //109389752419844
    }

    void show(){
        infoLayout.setVisibility(View.VISIBLE);
        toolbarText.setText("Salisbury Zoo Information");
    }
    void hide(){
        infoLayout.setVisibility(View.GONE);
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}

