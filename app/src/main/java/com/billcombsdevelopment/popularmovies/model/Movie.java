/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Movie object that stores information about individual movie titles
 */

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @SerializedName("id")
    @Expose
    private final Integer mId;
    @SerializedName("title")
    @Expose
    private final String mTitle;
    @SerializedName("poster_path")
    @Expose
    private final String mPosterPath;
    @SerializedName("overview")
    @Expose
    private final String mSynopsis;
    @SerializedName("release_date")
    @Expose
    private final String mReleaseDate;
    @SerializedName("vote_average")
    @Expose
    private final Float mUserRating;
    @SerializedName("backdrop_path")
    @Expose
    private final String mBackdropPath;
    @SerializedName("vote_count")
    @Expose
    private final Integer mVoteCount;

    public Movie(Integer id, String name, String posterPath, String backdropPath, String synopsis,
                 String releaseDate, Float userRating, Integer voteCount) {
        this.mId = id;
        this.mTitle = name;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mSynopsis = synopsis;
        this.mReleaseDate = releaseDate;
        this.mUserRating = userRating;
        this.mVoteCount = voteCount;
    }

    // Private constructor for Parcel
    private Movie(Parcel source) {
        this.mId = source.readInt();
        this.mTitle = source.readString();
        this.mPosterPath = source.readString();
        this.mBackdropPath = source.readString();
        this.mSynopsis = source.readString();
        this.mReleaseDate = source.readString();
        this.mUserRating = source.readFloat();
        this.mVoteCount = source.readInt();
    }

    public Integer getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public Float getUserRating() {
        return mUserRating;
    }

    public Integer getVoteCount() {
        return mVoteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeFloat(mUserRating);
        dest.writeInt(mVoteCount);
    }
}