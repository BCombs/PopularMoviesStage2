/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.network.NetworkRequests;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import java.util.List;

public class MainActivityPresenter implements PopularMoviesContract.Presenter,
        PopularMoviesContract.MovieDataListener {
    private PopularMoviesContract.View mMainActivityView;
    private NetworkRequests mNetworkRequests;

    public MainActivityPresenter(PopularMoviesContract.View view) {
        this.mMainActivityView = view;
        this.mNetworkRequests = new NetworkRequests(this);
    }

    @Override
    public void getMovieData(String sortOption) {
        mNetworkRequests.makeNetworkRequest(sortOption);
    }

    @Override
    public void onSuccess(List<Movie> movieList) {
        mMainActivityView.onSuccess(movieList);
    }

    @Override
    public void onFailure(String message) {
        mMainActivityView.onFailure(message);
    }
}
