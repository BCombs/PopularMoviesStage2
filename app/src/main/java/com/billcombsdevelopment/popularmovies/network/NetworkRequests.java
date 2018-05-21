/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.network;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieData;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetworkRequests {

    private final String MOST_POPULAR_QUERY = "Most Popular";
    private final String TOP_RATED_QUERY = "Top Rated";
    private PopularMoviesContract.MovieDataListener mListener;

    public NetworkRequests(PopularMoviesContract.MovieDataListener listener) {
        this.mListener = listener;
    }

    public void makeNetworkRequest(String sortOption) {
        Retrofit client = RetrofitClient.getRetrofitClient();

        MovieApi movieApi = client.create(MovieApi.class);

        Call<MovieData> call;

        switch (sortOption) {
            case MOST_POPULAR_QUERY:
                call = movieApi.getPopularMovies();
                break;
            case TOP_RATED_QUERY:
                call = movieApi.getTopRatedMovies();
                break;
            default:
                call = movieApi.getPopularMovies();
        }

        call.enqueue(new Callback<MovieData>() {
            @Override
            public void onResponse(Call<MovieData> call, Response<MovieData> response) {
                MovieData movieData = response.body();
                List<Movie> movieList = movieData.getMovies();
                mListener.onSuccess(movieList);
            }

            @Override
            public void onFailure(Call<MovieData> call, Throwable t) {
                mListener.onFailure(t.getMessage());
                call.cancel();
            }
        });
    }
}