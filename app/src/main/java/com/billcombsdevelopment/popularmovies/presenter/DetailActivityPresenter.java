/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import android.content.Context;
import android.util.Log;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieReview;
import com.billcombsdevelopment.popularmovies.model.MovieReviewData;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.billcombsdevelopment.popularmovies.model.MovieTrailerData;
import com.billcombsdevelopment.popularmovies.network.NetworkRequests;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivityPresenter implements PopularMoviesContract.DetailPresenter,
        PopularMoviesContract.DetailDataListener {
    private final PopularMoviesContract.DetailView mDetailView;
    private final NetworkRequests mNetworkRequests;
    private final Movie mMovie;
    private boolean mIsFavorite = false;

    public DetailActivityPresenter(Movie movie, PopularMoviesContract.DetailView view) {
        this.mMovie = movie;
        mDetailView = view;
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

    public void setAsFavorite() {
        mIsFavorite = true;
    }

    public void removeFromFavorites() {
        mIsFavorite = false;
    }

    public boolean isFavorite() {
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
        List<MovieTrailer> trailerList = trailerData.getMovieTrailers();
        mDetailView.onTrailerSuccess(trailerList);
    }

    @Override
    public void onReviewSuccess(MovieReviewData reviewData) {
        List<MovieReview> movieReviews = reviewData.getMovieReviews();
        mDetailView.onReviewSuccess(movieReviews);
    }

    @Override
    public void onFailure(String message) {
        mDetailView.onFailure(message);
    }
}