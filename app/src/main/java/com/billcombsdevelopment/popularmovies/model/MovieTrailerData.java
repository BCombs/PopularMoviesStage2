/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieTrailerData {
    @SerializedName("id")
    @Expose
    private final String mId = "";
    @SerializedName("results")
    @Expose
    private List<MovieTrailer> mMovieTrailers = null;

    public String getId() {
        return mId;
    }

    public List<MovieTrailer> getMovieTrailers() {
        return mMovieTrailers;
    }
}
