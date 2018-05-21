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

    private final PopularMoviesContract.View mMainActivityView;
    private final NetworkRequests mNetworkRequests;
    private MovieData mMovieData = null;
    private List<Movie> mMovieList = new ArrayList<>();
    private boolean mIsLoading = false;
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
        mIsLoading = true;
        mNetworkRequests.makeNetworkRequest(sortOption, mCurrentPage);
    }

    @Override
    public void loadMoreMovieData() {
        mIsLoading = true;
        mCurrentPage++;
        mNetworkRequests.additionalRequest(mSortOption, mCurrentPage);
    }

    public String getSortOption() {
        return mSortOption;
    }

    public void setSortOption(String sortOption) {
        this.mSortOption = sortOption;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLoading() {
        return mIsLoading;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLastPage() {
        return mCurrentPage == mMovieData.getTotalPages();
    }

    public int getPageSize() {
        int PAGE_SIZE = 20;
        return PAGE_SIZE;
    }

    public ArrayList<Movie> getMovieList() {
        return (ArrayList<Movie>) mMovieList;
    }

    public void setMovieList(ArrayList<Movie> movieList) {
        this.mMovieList = movieList;
    }

    @Override
    public void onSuccess(MovieData movieData) {
        mIsLoading = false;
        mMovieData = movieData;
        mMovieList.clear();
        mMovieList = movieData.getMovies();
        mMainActivityView.onSuccess(mMovieList);
    }

    @Override
    public void onFailure(String message) {
        mIsLoading = false;
        mMainActivityView.onFailure(message);
    }

    @Override
    public void onUpdate(MovieData movieData) {
        mCurrentPage++;
        mIsLoading = false;
        List<Movie> movieList = movieData.getMovies();
        mMovieList.addAll(movieList);
        mMainActivityView.onUpdate(mMovieList);
    }
}
