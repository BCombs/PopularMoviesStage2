/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieTrailer {
    @SerializedName("id")
    @Expose
    private final String mId;
    @SerializedName("key")
    @Expose
    private final String mKey;
    @SerializedName("name")
    @Expose
    private final String mName;

    public MovieTrailer(String id, String key, String name) {
        this.mId = id;
        this.mKey = key;
        this.mName = name;
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
}
