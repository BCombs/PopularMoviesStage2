/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import android.content.Context;
import android.util.Log;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.Movie;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailActivityPresenter {
    private final Movie mMovie;
    private boolean mIsFavorite = false;

    public DetailActivityPresenter(Movie movie) {
        this.mMovie = movie;
    }

    public String getMovieTitle() {
        return mMovie.getTitle();
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

    public void setAsFavorite() {
        mIsFavorite = true;
    }

    public void removeFromFavorites() {
        mIsFavorite = false;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }
}