/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieData;
import com.billcombsdevelopment.popularmovies.network.NetworkRequests;
import com.billcombsdevelopment.popularmovies.view.PopularMoviesContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPresenter implements PopularMoviesContract.Presenter,
        PopularMoviesContract.MovieDataListener {

    private final int PAGE_SIZE = 20;
    private PopularMoviesContract.View mMainActivityView;
    private NetworkRequests mNetworkRequests;
    private MovieData mMovieData = null;
    private List<Movie> mMovieList = new ArrayList<>();
    private boolean isLoading = false;
    private int mCurrentPage = 1;
    private String mSortOption = "";

    public MainActivityPresenter(PopularMoviesContract.View view) {
        this.mMainActivityView = view;
        this.mNetworkRequests = new NetworkRequests(this);
    }

    @Override
    public void loadMovieData(String sortOption) {
        mCurrentPage = 1;
        mSortOption = sortOption;
        isLoading = true;
        mNetworkRequests.makeNetworkRequest(sortOption, mCurrentPage);
    }

    @Override
    public void loadMoreMovieData() {
        mCurrentPage++;
        mNetworkRequests.additionalRequest(mSortOption, mCurrentPage);
    }


    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLastPage() {
        return mCurrentPage == mMovieData.getTotalPages();
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void onSuccess(MovieData movieData) {
        isLoading = false;
        mCurrentPage++;
        mMovieData = movieData;
        mMovieList = movieData.getMovies();
        mMainActivityView.onSuccess(mMovieList);
    }

    @Override
    public void onFailure(String message) {
        isLoading = false;
        mMainActivityView.onFailure(message);
    }

    @Override
    public void onUpdate(MovieData movieData) {
        List<Movie> movieList = movieData.getMovies();
        for (Movie movie : movieList) {
            mMovieList.add(movie);
        }
        mMainActivityView.onUpdate(mMovieList);
    }
}
