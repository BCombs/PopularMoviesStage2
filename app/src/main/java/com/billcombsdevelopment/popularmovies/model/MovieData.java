/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieData {

    @SerializedName("page")
    @Expose
    private Integer mPage;
    @SerializedName("total_pages")
    @Expose
    private Integer mTotalPages;
    @SerializedName("results")
    @Expose
    private List<Movie> mMovies = null;

    public List<Movie> getMovies() { return mMovies; }

    public Integer getPage() { return mPage; }

    public Integer getTotalPages() { return mTotalPages; }

}