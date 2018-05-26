/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.network;

import android.support.annotation.NonNull;

import com.billcombsdevelopment.popularmovies.model.MovieData;
import com.billcombsdevelopment.popularmovies.model.MovieReviewData;
import com.billcombsdevelopment.popularmovies.model.MovieTrailerData;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetworkRequests {

    private final String MOST_POPULAR_QUERY = "Most Popular";
    private final String TOP_RATED_QUERY = "Top Rated";

    private PopularMoviesContract.MovieDataListener mListener = null;
    private PopularMoviesContract.DetailDataListener mDetailListener = null;

    public NetworkRequests(PopularMoviesContract.MovieDataListener listener) {
        this.mListener = listener;
    }

    public NetworkRequests(PopularMoviesContract.DetailDataListener detailListener) {
        this.mDetailListener = detailListener;
    }

    public void makeNetworkRequest(String sortOption, int pageNumber) {
        Retrofit client = RetrofitClient.getRetrofitClient();

        MovieApi movieApi = client.create(MovieApi.class);

        Call<MovieData> call;

        switch (sortOption) {
            case MOST_POPULAR_QUERY:
                call = movieApi.getPopularMovies(pageNumber);
                break;
            case TOP_RATED_QUERY:
                call = movieApi.getTopRatedMovies(pageNumber);
                break;
            default:
                call = movieApi.getPopularMovies(pageNumber);
        }

        call.enqueue(new Callback<MovieData>() {
            @Override
            public void onResponse(@NonNull Call<MovieData> call, @NonNull Response<MovieData> response) {
                MovieData movieData = response.body();
                mListener.onMovieSuccess(movieData);
            }

            @Override
            public void onFailure(@NonNull Call<MovieData> call, @NonNull Throwable t) {
                mListener.onFailure(t.getMessage());
                call.cancel();
            }
        });
    }

    public void additionalRequest(String sortOption, int pageNumber) {
        Retrofit client = RetrofitClient.getRetrofitClient();

        MovieApi movieApi = client.create(MovieApi.class);

        Call<MovieData> call;

        switch (sortOption) {
            case MOST_POPULAR_QUERY:
                call = movieApi.getPopularMovies(pageNumber);
                break;
            case TOP_RATED_QUERY:
                call = movieApi.getTopRatedMovies(pageNumber);
                break;
            default:
                call = movieApi.getPopularMovies(pageNumber);
        }

        call.enqueue(new Callback<MovieData>() {
            @Override
            public void onResponse(@NonNull Call<MovieData> call,
                                   @NonNull Response<MovieData> response) {
                MovieData movieData = response.body();
                mListener.onMovieUpdate(movieData);
            }

            @Override
            public void onFailure(@NonNull Call<MovieData> call, @NonNull Throwable t) {
                mListener.onFailure(t.getMessage());
                call.cancel();
            }
        });
    }

    public void trailerRequest(String movieId) {
        Retrofit client = RetrofitClient.getRetrofitClient();

        MovieApi movieApi = client.create(MovieApi.class);

        Call<MovieTrailerData> call = movieApi.getMovieTrailers(movieId);
        call.enqueue(new Callback<MovieTrailerData>() {
            @Override
            public void onResponse(@NonNull Call<MovieTrailerData> call,
                                   @NonNull Response<MovieTrailerData> response) {

                MovieTrailerData trailerData = response.body();
                mDetailListener.onTrailerSuccess(trailerData);
            }

            @Override
            public void onFailure(@NonNull Call<MovieTrailerData> call, @NonNull Throwable t) {
                mDetailListener.onFailure(t.getMessage());
                call.cancel();
            }
        });
    }

    public void reviewRequest(String movieId) {
        Retrofit client = RetrofitClient.getRetrofitClient();

        MovieApi movieApi = client.create(MovieApi.class);

        Call<MovieReviewData> call = movieApi.getMovieReviews(movieId);
        call.enqueue(new Callback<MovieReviewData>() {
            @Override
            public void onResponse(@NonNull Call<MovieReviewData> call,
                                   @NonNull Response<MovieReviewData> response) {

                MovieReviewData reviewData = response.body();
                mDetailListener.onReviewSuccess(reviewData);
            }

            @Override
            public void onFailure(@NonNull Call<MovieReviewData> call, @NonNull Throwable t) {
                mDetailListener.onFailure(t.getMessage());
                call.cancel();
            }
        });
    }
}