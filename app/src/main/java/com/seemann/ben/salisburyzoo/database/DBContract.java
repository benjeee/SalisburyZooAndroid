package com.seemann.ben.salisburyzoo.database;

import android.provider.BaseColumns;

/**
 * Created by Ben on 12/31/2016.
 */

public class DBContract {
    private DBContract() {}

    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "animals";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SN = "sn";
        public static final String COLUMN_ADDED = "added";
        public static final String COLUMN_HABITAT = "habitat";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DISCOVERED = "discovered";
    }
}
