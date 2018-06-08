/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.billcombsdevelopment.popularmovies.database.FavoritesDbContract.MovieEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table if it doesn't exist
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_NAME_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_NAME_SYNOPSIS + " TEXT, " +
                MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_NAME_VOTE_COUNT + " INTEGER, " +
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
