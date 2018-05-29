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

    /**
     * Makes the initial request for movie data and sets/resets the current page to 1
     *
     * @param sortOption
     */
    @Override
    public void loadMovieData(String sortOption) {
        mCurrentPage = 1;
        mSortOption = sortOption;
        mIsLoading = true;
        mNetworkRequests.makeNetworkRequest(sortOption, mCurrentPage);
    }

    /**
     * For pagination. Makes additional requests when the user reaches the end of the data available
     */
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

    /**
     * Flag to indicate whether more data is currently being loaded. If it is, ignore other
     * requests for more data.
     *
     * @return
     */
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

    /**
     * When the activity has been destroyed, for example, device rotation, this method is called to
     * restore the state of the presenter
     *
     * @param movieList   - The list of movies the user was currently viewing
     * @param currentPage - The current page that was fetched
     * @param totalPages  - The total number of pages available
     * @param sortOption  - The sort option the user was on
     */
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

    /**
     * Callback for initial data. Get the list of movies and pass
     * it to the UI for display making sure to clear the previous data
     * (ex. switched from Most Popular to Top rated. Clear most popular and add Top Rated).
     *
     * @param movieData - Original data object retrieved from network request
     */
    @Override
    public void onMovieSuccess(MovieData movieData) {
        if (movieData != null) {
            mIsLoading = false;
            mTotalPages = movieData.getTotalPages();
            mMovieList.clear();
            mMovieList = movieData.getMovies();
        }
        mMainActivityView.onMovieSuccess(mMovieList);
    }

    @Override
    public void onFailure(String message) {
        mIsLoading = false;
        mMainActivityView.onFailure(message);
    }

    /**
     * Callback for pagination requests. Appends the new movies to the current List
     *
     * @param movieData - Original data object retrieved from network request
     */
    @Override
    public void onMovieUpdate(MovieData movieData) {
        mCurrentPage++;
        mIsLoading = false;
        List<Movie> movieList = movieData.getMovies();
        mMovieList.addAll(movieList);
        mMainActivityView.onUpdate(mMovieList);
    }
}
