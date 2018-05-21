/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import com.billcombsdevelopment.popularmovies.model.Movie;

import java.util.List;

/*
 * Contract interface with inner interfaces for MVP pattern learned at
 * https://medium.com/@cervonefrancesco/model-view-presenter-android-guidelines-94970b430ddf
 */
public interface PopularMoviesContract {
    interface View {
        void onSuccess(List<Movie> movieList);

        void onFailure(String message);
    }

    interface Presenter {
        void getMovieData(String sortOption);
    }

    interface MovieDataListener {
        void onSuccess(List<Movie> movieList);

        void onFailure(String message);
    }
}
