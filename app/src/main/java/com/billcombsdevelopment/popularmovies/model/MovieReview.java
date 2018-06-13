package com.billcombsdevelopment.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReview implements Parcelable {

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
    @SerializedName("author")
    @Expose
    private final String mAuthor;
    @SerializedName("content")
    @Expose
    private final String mContent;
    @SerializedName("id")
    @Expose
    private final String mId;
    @SerializedName("url")
    @Expose
    private final String mUrl;

    public MovieReview(String author, String content, String id, String url) {
        this.mAuthor = author;
        this.mContent = content;
        this.mId = id;
        this.mUrl = url;
    }

    private MovieReview(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mId = in.readString();
        mUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mId);
        dest.writeString(mUrl);
    }

    @Override
    public int describeContents() {
        return 0;
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
