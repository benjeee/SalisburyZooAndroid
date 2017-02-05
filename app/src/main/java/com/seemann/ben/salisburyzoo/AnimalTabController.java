package com.seemann.ben.salisburyzoo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.seemann.ben.salisburyzoo.barcode.BarcodeCaptureActivity;
import com.seemann.ben.salisburyzoo.database.DBContract;
import com.seemann.ben.salisburyzoo.database.DBHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ben on 1/23/2017.
 */

class AnimalTabController {

    private LinearLayout inventory_layout;
    private AnimalAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton scanBarcodeButton;
    private TextView toolbarText;
    private LinearLayout animalDetails;
    private EditText animalSearch;
    private Button searchButton;
    private TextView animalDetailsText;
    private ImageView animalDetailsImg;
    private double width;
    private TextView animalsObtainedView;
    private DBHelper dbh;
    private StorageReference storageRef;
    private MainActivity context;
    private ArrayList<Animal> animals;
    private FirebaseDatabase db;
    private Button backButton;
    private Button tts;
    private TextToSpeech ttsobj;
    private Button showInfoButton;
    private LinearLayout infoPage;
    private final String LOG_TAG = "Animal Tab Controller";
    private int discoveredCount;

    AnimalTabController(final MainActivity context, FirebaseDatabase db, StorageReference storageRef){
        this.context = context;
        this.db = db;
        this.storageRef = storageRef;
        dbh = new DBHelper(context);

        discoveredCount=0;

        Toolbar myToolbar = (Toolbar) context.findViewById(R.id.toolbar);
        context.setSupportActionBar(myToolbar);
        context.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarText = (TextView) context.findViewById(R.id.toolbar_title);

        inventory_layout = (LinearLayout) context.findViewById(R.id.inventory_layout);
        scanBarcodeButton = (FloatingActionButton) context.findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), BarcodeCaptureActivity.class);
                context.startActivityForResult(intent, context.BARCODE_READER_REQUEST_CODE);
            }
        });
        ListView inventory_list = (ListView) context.findViewById(R.id.inventory_list);
        animalDetails = (LinearLayout) context.findViewById(R.id.animal_details);
        animalDetailsText = (TextView) context.findViewById(R.id.animal_details_text);
        animalDetailsImg = (ImageView) context.findViewById(R.id.animal_details_img);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        animalDetailsImg.getLayoutParams().height = (int)width;
        animalDetailsImg.getLayoutParams().width = (int)width;
        backButton = (Button) context.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnimalsTab();
            }
        });
        tts = (Button) context.findViewById(R.id.tts_animal);
        ttsobj=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

        showInfoButton = (Button) context.findViewById(R.id.info_button);
        showInfoButton.setOnClickListener(new View.OnClickListener() {
            private int isShowing;
            @Override
            public void onClick(View view) {
                inventory_layout.setVisibility(View.GONE);
                scanBarcodeButton.setVisibility(View.GONE);
                toolbarText.setText("App Information");
                infoPage.setVisibility(View.VISIBLE);
                showInfoButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
            }
        });
        infoPage = (LinearLayout) context.findViewById(R.id.info_animals_layout);
        ImageView infoImg = (ImageView) context.findViewById(R.id.info_animals_img);
        TextView infoTxt = (TextView) context.findViewById(R.id.info_animals_txt);
        Picasso.with(context).load(R.drawable.logo).into(infoImg);
        infoTxt.setText(
                "Welcome to the Salisbury Zoo Mobile App!\n\n" +
                "As you explore the zoo, you will notice some QR codes posted at different exhibits. You can collect animals by scanning these QR codes by pressing the plus button at the bottom right in the previous screen!\n\n" +
                        "Newly discovered animals will be hidden until you click on them to reveal their information. Collect as many animals as you can!\n\n"+
                        "For more zoo information, check the News and Web tabs from the bottom menu."
        );

        animalsObtainedView = (TextView)context.findViewById(R.id.animals_obtained);
        animalsObtainedView.getLayoutParams().height = metrics.heightPixels;
        animalsObtainedView.getLayoutParams().width = metrics.widthPixels;

        animals = new ArrayList();
        adapter = new AnimalAdapter(context, animals, storageRef, dbh, this);
        inventory_list.setAdapter(adapter);

        animalSearch = (EditText) context.findViewById(R.id.animal_search);
        animalSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        searchButton = (Button) context.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            private int isShowing;
            @Override
            public void onClick(View view) {
                if(isShowing == 1){
                    animalSearch.setVisibility(View.GONE);
                    toolbarText.setVisibility(View.VISIBLE);
                    animalSearch.setText(null);
                    isShowing = 0;
                }else {
                    animalSearch.setVisibility(View.VISIBLE);
                    toolbarText.setVisibility(View.GONE);
                    isShowing = 1;
                }
            }
        });
        bottomNavigationView = (BottomNavigationView)
                context.findViewById(R.id.bottom_navigation);

    }

    void incrementDiscovered(){
        discoveredCount++;
    }

    void updateToolbarText(){
        toolbarText.setText("Your Animals ("+discoveredCount+")");
    }
    void showObtained(){
        animalsObtainedView.setVisibility(View.VISIBLE);
        animalsObtainedView.bringToFront();
    }

    void addFlamingo(){
        Log.d(LOG_TAG, "ADDING THE DANG FLAMINGO DANgIT");
        Animal flamingo = new Animal();
        flamingo.setName("American Flamingo");
        flamingo.setDescription("Flamingos are nomadic, moving from lake to lake following the wet/dry cycle of salt flats. Crustaceans, algae, and diatoms consumed from these environments are responsible for their pinkish color.");
        flamingo.setImage("flamingo600.jpg");
        flamingo.setSn("Phoenicopterus ruber");
        flamingo.setHabitat("Highland salt lakes, brackish estuaries, and coastal marshes.");
        flamingo.setStatus("Least concern but making conservation areas for them is difficult because of their nomadic lifestyle.");
        flamingo.setAdded("");
        flamingo.setDiscovered(false);
        dbh.addAnimal(flamingo);
        notifyAdapter();
    }
    void scanExhibit
            (String exhibitName){

        DatabaseReference myRef = db.getReference("Exhibits").child(exhibitName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                for (DataSnapshot animalSnapshot : dataSnapshot.getChildren()) {
                    String key = animalSnapshot.getKey();
                    final Animal animal = animalSnapshot.getValue(Animal.class);
                    animal.setName(key);
                    animal.setDiscovered(false);
                    long rowid = dbh.addAnimal(animal);
                    if(rowid > 0){
                        animal.setRowid(rowid);
                        animals.add(0, animal);
                        total++;
                    }
                }
                adapter.notifyDataSetChanged();
                if(total == 0){
                    animalsObtainedView.setText("No new animals discovered!");
                } else if(total == 1) {
                    animalsObtainedView.setText("You discovered a new animal!");
                } else {
                    animalsObtainedView.setText("You discovered " + total + " new animals!");
                }
                animalsObtainedView.postDelayed(new Runnable() {
                    public void run() {
                        animalsObtainedView.setVisibility(View.GONE);
                        animalsObtainedView.setText(null);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        showAnimalsTab();
                    }
                }, 3000);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
            }
        });
        adapter.notifyDataSetChanged();
    }

    void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    void showAnimalDetails(final Animal animal){
        animalDetailsImg.setImageDrawable(null);
        inventory_layout.setVisibility(View.GONE);
        scanBarcodeButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        animalSearch.setVisibility(View.GONE);
        showInfoButton.setVisibility(View.GONE);
        animalDetails.setVisibility(View.VISIBLE);
        toolbarText.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        tts.setVisibility(View.VISIBLE);
        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsobj.speak(animal.getTTSString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        animalDetailsText.setText(Html.fromHtml(animal.getDetailsString()));
        toolbarText.setText(animal.getName());
        storageRef.child(animal.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).resize((int) width, (int) width).centerCrop().into(animalDetailsImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
    void showAnimalsTab(){
        inventory_layout.setVisibility(View.VISIBLE);
        scanBarcodeButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        showInfoButton.setVisibility(View.VISIBLE);
        infoPage.setVisibility(View.GONE);
        animalDetails.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        tts.setVisibility(View.GONE);
        ttsobj.stop();
        toolbarText.setVisibility(View.VISIBLE);
        toolbarText.setText("Your Animals ("+discoveredCount+")");
        animalSearch.setText(null);
    }
    void hideAnimalsTab(){
        inventory_layout.setVisibility(View.GONE);
        scanBarcodeButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        showInfoButton.setVisibility(View.GONE);
        infoPage.setVisibility(View.GONE);
        animalSearch.setVisibility(View.GONE);
        animalDetails.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        tts.setVisibility(View.GONE);
        ttsobj.stop();
    }

    void populateAnimalListFromDB(){
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] projection = {
                "rowid",
                DBContract.DBEntry.COLUMN_NAME,
                DBContract.DBEntry.COLUMN_SN,
                DBContract.DBEntry.COLUMN_ADDED,
                DBContract.DBEntry.COLUMN_HABITAT,
                DBContract.DBEntry.COLUMN_DESCRIPTION,
                DBContract.DBEntry.COLUMN_STATUS,
                DBContract.DBEntry.COLUMN_IMAGE,
                DBContract.DBEntry.COLUMN_DISCOVERED
        };
        String sortOrder =  " rowid ASC";
        Cursor cursor = db.query(
                DBContract.DBEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while(cursor.moveToNext()) {
            Animal animal = new Animal();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_NAME));
            String sn = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_SN));
            String habitat = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_HABITAT));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_STATUS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_DESCRIPTION));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_IMAGE));
            int discovered = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.DBEntry.COLUMN_DISCOVERED));
            long rowid = cursor.getLong(cursor.getColumnIndex("rowid"));
            animal.setName(name);
            animal.setDescription(description);
            animal.setImage(image);
            animal.setRowid(rowid);
            animal.setSn(sn);
            animal.setHabitat(habitat);
            animal.setStatus(status);
            if(discovered == 0){
                animal.setDiscovered(false);
            } else {
                incrementDiscovered();
                updateToolbarText();
                animal.setDiscovered(true);
            }
            animals.add(0, animal);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }
}
