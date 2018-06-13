/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieTrailer implements Parcelable {
    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };
    @SerializedName("id")
    @Expose
    private final String mId;
    @SerializedName("key")
    @Expose
    private final String mKey;
    @SerializedName("name")
    @Expose
    private final String mName;
    @SerializedName("type")
    @Expose
    private String mType;

    public MovieTrailer(String id, String key, String name, String type) {
        this.mId = id;
        this.mKey = key;
        this.mName = name;
        this.mType = type;
    }

    private MovieTrailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
