/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieData;

import java.util.List;

/*
 * Contract interface with inner interfaces for MVP pattern learned at
 * https://medium.com/@cervonefrancesco/model-view-presenter-android-guidelines-94970b430ddf
 */
public interface PopularMoviesContract {
    interface View {
        void onSuccess(List<Movie> movieList);

        void onFailure(String message);

        void onUpdate(List<Movie> movieList);
    }

    interface Presenter {
        void loadMovieData(String sortOption);

        void loadMoreMovieData();
    }

    interface MovieDataListener {
        void onSuccess(MovieData movieData);

        void onFailure(String message);

        void onUpdate(MovieData movieData);
    }
}
