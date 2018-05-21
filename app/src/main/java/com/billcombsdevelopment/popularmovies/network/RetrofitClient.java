package com.billcombsdevelopment.popularmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit sRetrofit = null;
    private static String BASE_URL = "http://api.themoviedb.org/3/";

    private RetrofitClient(){}

    public static Retrofit getRetrofitClient() {
        if(sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
