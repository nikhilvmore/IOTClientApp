package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2; // <-- INCREMENT THIS
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MACHINE_NAME + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TEMPERATURE + " REAL," + // Use REAL for decimal values
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SPEED + " INTEGER," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ELECTRICITY_CONSUMPTION + " REAL," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " INTEGER)"; // Use INTEGER for Unix timestamp

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Reads all entries from the database and returns them as a list of MyData objects.
     * @return A List of MyData objects.
     */
    public List<MyData> getAllEntries() {
        List<MyData> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query all rows from the table, ordered by timestamp descending
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (null to return all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                    // don't filter by row groups
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " DESC" // The sort order
        );

        // Loop through all the rows and add them to the list
        while (cursor.moveToNext()) {
            String machineName = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MACHINE_NAME));
            double temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TEMPERATURE));
            int speed = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SPEED));
            double electricityConsumption = cursor.getDouble(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ELECTRICITY_CONSUMPTION));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP));

            dataList.add(new MyData(machineName, temperature, speed, electricityConsumption, timestamp));
        }

        // Clean up by closing the cursor and database
        cursor.close();
        db.close();

        return dataList;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    // onDowngrade is also needed
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}