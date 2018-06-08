/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.database.FavoritesDbContract.MovieEntry;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieReview;
import com.billcombsdevelopment.popularmovies.model.MovieReviewData;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.billcombsdevelopment.popularmovies.model.MovieTrailerData;
import com.billcombsdevelopment.popularmovies.network.NetworkRequests;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivityPresenter implements PopularMoviesContract.DetailPresenter,
        PopularMoviesContract.DetailDataListener {

    private static final String TAG = DetailActivityPresenter.class.getSimpleName();
    private final PopularMoviesContract.DetailView mDetailView;
    private final NetworkRequests mNetworkRequests;
    private final Movie mMovie;
    private boolean mIsFavorite = false;

    // TEMPORARY UNTIL DAGGER 2 DEPENDENCY INJECTION
    private Context mContext;

    public DetailActivityPresenter(Movie movie, PopularMoviesContract.DetailView detailView,
                                   Context context) {
        this.mMovie = movie;
        mDetailView = detailView;
        mContext = context;
        mNetworkRequests = new NetworkRequests(this);
    }

    public String getMovieTitle() {
        return mMovie.getTitle();
    }

    public String getMovieId() {
        return mMovie.getId().toString();
    }

    /**
     * Takes the release date String and converts it to a date. Create an instance of a
     * Calendar and set it to the Date object. Return the year the movie was released.
     *
     * @return String releaseYear
     */
    public String getReleaseYear() {
        String releaseDate = mMovie.getReleaseDate();
        String releaseYear = "";
        Date date = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            date = dateFormat.parse(releaseDate);
        } catch (java.text.ParseException e) {
            Log.d("TAG", e.getMessage());
        }

        // Get an instance of Calendar and set to Date object
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            Integer year = calendar.get(Calendar.YEAR);
            releaseYear = "(" + year.toString() + ")";
        }

        return releaseYear;
    }

    public String getPosterUrl() {
        String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
        return BASE_POSTER_URL + mMovie.getPosterPath();
    }

    public String getBackdropUrl() {
        String BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w342/";
        return BASE_BACKDROP_URL + mMovie.getBackdropPath();
    }

    public String getMovieSynopsis() {
        return mMovie.getSynopsis();
    }

    public float getMovieRating() {
        return mMovie.getUserRating() / 2;
    }

    public String getTotalRatings(Context context) {
        int count = mMovie.getVoteCount();
        String totalRatings = context.getResources().getString(R.string.total_ratings);
        return "(" + count + " " + totalRatings + ")";
    }

    public void addToFavorites(Movie movie) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_NAME_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_NAME_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MovieEntry.COLUMN_NAME_SYNOPSIS, movie.getSynopsis());
        contentValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getUserRating());
        contentValues.put(MovieEntry.COLUMN_NAME_VOTE_COUNT, movie.getVoteCount());

        Log.d("ContentValues", contentValues.getAsString(MovieEntry.COLUMN_NAME_TITLE));

        Uri uri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
        Log.d(TAG, "Added " + uri);
    }

    public void deleteFromFavorites(Integer movieId) {
        Uri uri = MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId.toString()).build();
        String where = MovieEntry.COLUMN_NAME_MOVIE_ID + "=?";
        String[] selectionArgs = {movieId.toString()};
        int deletedRow = mContext.getContentResolver().delete(uri, where, selectionArgs);
        Log.d("deleteFromFavorites", "Deleted : " + deletedRow + " row/s.");
    }

    public void setAsFavorite() {
        mIsFavorite = true;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public boolean queryIsFavorite(Integer movieId) {
        Uri uri = MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId.toString()).build();
        Cursor cursor = mContext.getContentResolver()
                .query(uri,
                        null,
                        null,
                        null,
                        null,
                        null);

        if (cursor.getCount() > 0) {
            mIsFavorite = true;
        } else {
            mIsFavorite = false;
        }

        return mIsFavorite;
    }

    @Override
    public void loadTrailerData(String movieId) {
        mNetworkRequests.trailerRequest(movieId);
    }

    @Override
    public void loadReviewData(String movieId) {
        mNetworkRequests.reviewRequest(movieId);
    }

    @Override
    public void onTrailerSuccess(MovieTrailerData trailerData) {
        List<MovieTrailer> trailerList = new ArrayList<>();
        /*
         * Check if we successfully retrieved the data we wanted. If we did, fetch the trailers.
         * If trailerData is null (502 Bad Gateway response for example) return the empty list to
         * hide the trailer section from the UI.
         */
        if (trailerData != null) {
            trailerList = trailerData.getMovieTrailers();
        }
        mDetailView.onTrailerSuccess(trailerList);
    }

    @Override
    public void onReviewSuccess(MovieReviewData reviewData) {
        /*
         * Check if we successfully retrieved the data we wanted. If we did, fetch the reviews.
         * If reviewData is null (502 Bad Gateway response for example) return the empty list to
         * hide the review section from the UI.
         */
        List<MovieReview> movieReviews = new ArrayList<>();
        if (reviewData != null) {
            movieReviews = reviewData.getMovieReviews();
        }
        mDetailView.onReviewSuccess(movieReviews);
    }

    @Override
    public void onFailure(String message) {
        mDetailView.onFailure(message);
    }
}