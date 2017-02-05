package com.seemann.ben.salisburyzoo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seemann.ben.salisburyzoo.Animal;

/**
 * Created by Ben on 12/31/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.TABLE_NAME + " ( " +
                    DBContract.DBEntry.COLUMN_NAME + " TEXT UNIQUE, " +
                    DBContract.DBEntry.COLUMN_SN + " TEXT, " +
                    DBContract.DBEntry.COLUMN_ADDED + " TEXT, " +
                    DBContract.DBEntry.COLUMN_HABITAT + " TEXT, " +
                    DBContract.DBEntry.COLUMN_STATUS + " TEXT, " +
                    DBContract.DBEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    DBContract.DBEntry.COLUMN_IMAGE + " TEXT, " +
                    DBContract.DBEntry.COLUMN_DISCOVERED + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ZooDB.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void wipeTable(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public long addAnimal(Animal animal){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME, animal.getName());
        values.put(DBContract.DBEntry.COLUMN_ADDED, animal.getAdded());
        values.put(DBContract.DBEntry.COLUMN_HABITAT, animal.getHabitat());
        values.put(DBContract.DBEntry.COLUMN_SN, animal.getSn());
        values.put(DBContract.DBEntry.COLUMN_STATUS, animal.getStatus());
        values.put(DBContract.DBEntry.COLUMN_DESCRIPTION, animal.getDescription());
        values.put(DBContract.DBEntry.COLUMN_IMAGE, animal.getImage());
        values.put(DBContract.DBEntry.COLUMN_DISCOVERED, 0);

        Log.d("DBHELPER", "Entering animal " + animal.getName() + " with values " + animal.getDetailsString());
        // Insert the new row, returning the primary key value of the new row
        return db.insert(DBContract.DBEntry.TABLE_NAME, null, values);
    }

    public boolean discoverAnimal(Animal animal){
        SQLiteDatabase db = this.getReadableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_DISCOVERED, "1");
        // Which row to update, based on the title
        String selection = DBContract.DBEntry.COLUMN_NAME + " = " + animal.getName();
        String[] selectionArgs = { animal.getName() };
        int count = db.update(DBContract.DBEntry.TABLE_NAME, values, "rowid = "+animal.getRowid(),null);
        return true;
    }
}
