/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.billcombsdevelopment.popularmovies.database.FavoritesDbContract.MovieEntry;

public class FavoritesProvider extends ContentProvider {

    public static final int MOVIE = 1;
    public static final int MOVIE_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FavoritesDbContract.AUTHORITY, MovieEntry.TABLE_NAME, MOVIE);
        sUriMatcher.addURI(FavoritesDbContract.AUTHORITY, MovieEntry.TABLE_NAME + "/#", MOVIE_ID);
    }

    FavoritesDbHelper favoritesDbHelper;

    @Override
    public boolean onCreate() {
        favoritesDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();

        // Instantiate a query builder and set the table to 'movies'
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieEntry.TABLE_NAME);

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                // The queryBuilder is already set for this
                break;
            case MOVIE_ID:
                // Append a WHERE clause to queryBuilder
                queryBuilder.appendWhere(MovieEntry.COLUMN_NAME_MOVIE_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        // Execute query
        cursor = queryBuilder.query(db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                db.beginTransaction();
                try {
                    // Insert returns the row ID of the newly inserted row. -1 on error
                    long rowId = db.insert(MovieEntry.TABLE_NAME, null, values);
                    uri = ContentUris.withAppendedId(uri, rowId);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int deleted;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID:
                deleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
