/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieData;
import com.billcombsdevelopment.popularmovies.model.MovieReview;
import com.billcombsdevelopment.popularmovies.model.MovieReviewData;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.billcombsdevelopment.popularmovies.model.MovieTrailerData;

import java.util.List;

/*
 * Contract interface with inner interfaces for MVP pattern learned at
 * https://medium.com/@cervonefrancesco/model-view-presenter-android-guidelines-94970b430ddf
 */
public interface PopularMoviesContract {
    interface View {
        void onMovieSuccess(List<Movie> dataList);

        void onFailure(String message);

        void onUpdate(List<Movie> movieList);
    }

    interface Presenter {
        void loadMovieData(String sortOption);

        void loadMoreData();
    }

    interface MovieDataListener {
        void onMovieSuccess(MovieData movieData);

        void onFailure(String message);

        void onMovieUpdate(MovieData movieData);
    }

    interface DetailView {
        void onTrailerSuccess(List<MovieTrailer> trailerList);

        void onReviewSuccess(List<MovieReview> movieReviews);

        void onFailure(String message);
    }

    interface DetailPresenter {
        void loadTrailerData(String movieId);

        void loadReviewData(String movieId);
    }

    interface DetailDataListener {
        void onTrailerSuccess(MovieTrailerData trailerData);

        void onReviewSuccess(MovieReviewData reviewData);

        void onFailure(String message);
    }
}
