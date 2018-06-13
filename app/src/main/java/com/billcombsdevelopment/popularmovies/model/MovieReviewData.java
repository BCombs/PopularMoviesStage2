package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewData {
    @SerializedName("results")
    @Expose
    private List<MovieReview> mReviews = null;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("page")
    @Expose
    private Integer mPage;

    public List<MovieReview> getMovieReviews() {
        return mReviews;
    }
}
