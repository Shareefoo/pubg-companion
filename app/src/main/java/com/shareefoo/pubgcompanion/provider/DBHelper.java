package com.shareefoo.pubgcompanion.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shareefoo.pubgcompanion.provider.MatchContract.MatchEntry;

public class DBHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "pubg.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold the plants data
        final String SQL_CREATE_MATCHES_TABLE = "CREATE TABLE " + MatchEntry.TABLE_NAME + " (" +
                MatchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MatchEntry.COLUMN_PLACEMENT + " INTEGER NOT NULL, " +
                MatchEntry.COLUMN_TOTAL + " INTEGER NOT NULL, " +
                MatchEntry.COLUMN_KILLS + " INTEGER NOT NULL, " +
                MatchEntry.COLUMN_DAMAGE + " REAL NOT NULL, " +
                MatchEntry.COLUMN_DISTANCE + " REAL NOT NULL, " +
                MatchEntry.COLUMN_MODE + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_MATCHES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MatchEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
