/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FavoritesDbContract {

    public static final String AUTHORITY = "com.billcombsdevelopment.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Prevent instantiation
    private FavoritesDbContract() {
    }

    public static class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_VOTE_COUNT = "vote_count";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_BACKDROP_PATH = "backdrop_path";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();
    }
}
