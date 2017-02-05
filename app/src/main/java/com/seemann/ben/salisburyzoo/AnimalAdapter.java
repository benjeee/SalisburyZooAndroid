package com.seemann.ben.salisburyzoo;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.seemann.ben.salisburyzoo.database.DBHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ben on 12/27/2016.
 */

public class AnimalAdapter extends BaseAdapter implements Filterable {
    private final MainActivity context;
    private AnimalTabController animalTabController;
    private ArrayList<Animal> original;
    private ArrayList<Animal> filtered;
    private DBHelper dbh;
    private StorageReference storageRef;
    private LayoutInflater inflater;
    private AnimalFilter mFilter = new AnimalFilter();
    private double height;
    private double width;

    AnimalAdapter(MainActivity context, ArrayList<Animal> original, StorageReference storageRef, DBHelper dbh, AnimalTabController animalTabController) {
        this.animalTabController = animalTabController;
        this.context = context;
        this.filtered = original;
        this.original = original;
        this.storageRef = storageRef;
        this.dbh = dbh;
        this.inflater = context.getLayoutInflater();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        double gr = 1.61803398875;
        width = metrics.widthPixels;
        height = width/gr;
    }
    @Override
    public int getCount()
    {
        return filtered.size();
    }

    @Override
    public Object getItem(int i) {
        return filtered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        final TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final ImageView imgTitle = (ImageView) rowView.findViewById(R.id.img);
        imgTitle.getLayoutParams().height = (int)height + 1;
        imgTitle.getLayoutParams().width = (int)width;
        final Animal curr = filtered.get(position);
        if(curr.getDiscovered()) {
            setImageAndText(txtTitle, imgTitle, curr);
        }
        else {
            txtTitle.setText("?");
            Picasso.with(context).load(R.drawable.undiscovered).resize((int) width, (int) height).centerCrop().into(imgTitle);
        }
        imgTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animalTabController.showAnimalDetails(curr);
                if(!curr.getDiscovered()) {
                    animalTabController.incrementDiscovered();
                    curr.setDiscovered(true);
                    dbh.discoverAnimal(curr);
                    setImageAndText(txtTitle, imgTitle, curr);
                }
            }
        });
        return rowView;
    }

    private void setImageAndText(final TextView txtTitle, final ImageView imgTitle, final Animal curr){
        txtTitle.setText(curr.getName());
        storageRef.child(curr.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).resize((int) width, (int) height).centerCrop().into(imgTitle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }


    public Filter getFilter() {
        return mFilter;
    }

    private class AnimalFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Animal> list = original;

            int count = list.size();
            final ArrayList<Animal> nlist = new ArrayList<Animal>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (ArrayList<Animal>) results.values;
            notifyDataSetChanged();
        }

    }


}
