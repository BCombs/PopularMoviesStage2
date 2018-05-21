/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.network;

import com.billcombsdevelopment.popularmovies.BuildConfig;
import com.billcombsdevelopment.popularmovies.model.MovieData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieApi {

    String popularMovies = "movie/popular?api_key=" + BuildConfig.API_KEY;
    String topRatedMovies = "movie/top_rated?api_key=" + BuildConfig.API_KEY;

    @GET(popularMovies)
    Call<MovieData> getPopularMovies();

    @GET(topRatedMovies)
    Call<MovieData> getTopRatedMovies();
}

