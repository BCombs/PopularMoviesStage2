package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReview {

    @SerializedName("author")
    @Expose
    private String mAuthor;

    @SerializedName("content")
    @Expose
    private String mContent;

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("url")
    @Expose
    private String mUrl;

    public MovieReview(String author, String content, String id, String url) {
        this.mAuthor = author;
        this.mContent = content;
        this.mId = id;
        this.mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }
}
