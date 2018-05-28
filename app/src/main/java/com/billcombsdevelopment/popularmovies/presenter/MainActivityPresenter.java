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
    private final int PAGE_SIZE = 20;
    private List<Movie> mMovieList = new ArrayList<>();
    private boolean mIsLoading = false;
    private int mCurrentPage = 1;
    private int mTotalPages = 1;
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
    public void loadMoreData() {
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

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean isLastPage() {
        return mCurrentPage == mTotalPages;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public void onRestore(ArrayList<Movie> movieList, int currentPage, int totalPages,
                          String sortOption) {
        this.mMovieList = movieList;
        this.mCurrentPage = currentPage;
        this.mTotalPages = totalPages;
        this.mSortOption = sortOption;
    }

    public ArrayList<Movie> getMovieList() {
        return (ArrayList<Movie>) mMovieList;
    }

    @Override
    public void onMovieSuccess(MovieData movieData) {
        mIsLoading = false;
        mTotalPages = movieData.getTotalPages();
        mMovieList.clear();
        mMovieList = movieData.getMovies();
        mMainActivityView.onMovieSuccess(mMovieList);
    }

    @Override
    public void onFailure(String message) {
        mIsLoading = false;
        mMainActivityView.onFailure(message);
    }

    @Override
    public void onMovieUpdate(MovieData movieData) {
        mCurrentPage++;
        mIsLoading = false;
        List<Movie> movieList = movieData.getMovies();
        mMovieList.addAll(movieList);
        mMainActivityView.onUpdate(mMovieList);
    }
}
