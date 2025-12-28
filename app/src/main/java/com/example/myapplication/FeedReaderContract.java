package com.example.myapplication;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    // To prevent instantiation, make the constructor private
    private FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_MACHINE_NAME = "machine_name";
        public static final String COLUMN_NAME_TEMPERATURE = "temperature";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_ELECTRICITY_CONSUMPTION = "electricity_consumption";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

}