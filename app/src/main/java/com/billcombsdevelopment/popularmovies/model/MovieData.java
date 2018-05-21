/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieData {

    @SerializedName("results")
    @Expose
    private final List<Movie> mMovies = null;

    @SerializedName("total_pages")
    @Expose
    private Integer mTotalPages;

    public List<Movie> getMovies() {
        return mMovies;
    }

    public Integer getTotalPages() {
        return mTotalPages;
    }

}