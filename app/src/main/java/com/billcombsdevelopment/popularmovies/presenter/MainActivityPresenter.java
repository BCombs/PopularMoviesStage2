/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.billcombsdevelopment.popularmovies.database.FavoritesDbContract;
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
    private final String MOST_POPULAR = "Most Popular";
    private final String TOP_RATED = "Top Rated";
    private final String FAVORITES = "Favorites";
    private List<Movie> mMovieList = new ArrayList<>();
    private boolean mIsLoading = false;
    private int mCurrentPage = 1;
    private int mTotalPages = 1;
    private String mCurrentSortOption = "";

    // TEMPORARY UNTIL DAGGER 2 DEPENDENCY INJECTION
    private Context mContext;

    public MainActivityPresenter(PopularMoviesContract.View view, Context context) {
        mMainActivityView = view;
        mContext = context;
        mNetworkRequests = new NetworkRequests(this);
    }

    /**
     * Makes the initial request for movie data and sets/resets the current page to 1
     *
     * @param sortOption - Most Popular, Top Rated, Favorites
     */
    @Override
    public void loadMovieData(String sortOption) {
        switch (sortOption) {
            case MOST_POPULAR:
                mCurrentPage = 1;
                mCurrentSortOption = MOST_POPULAR;
                mIsLoading = true;
                mNetworkRequests.makeNetworkRequest(MOST_POPULAR, mCurrentPage);
                break;
            case TOP_RATED:
                mCurrentPage = 1;
                mCurrentSortOption = TOP_RATED;
                mIsLoading = true;
                mNetworkRequests.makeNetworkRequest(TOP_RATED, mCurrentPage);
                break;
            case FAVORITES:
                mCurrentSortOption = FAVORITES;
                new FavoritesQueryTask().execute();
                break;
            default:
                mCurrentPage = 1;
                mNetworkRequests.makeNetworkRequest(MOST_POPULAR, mCurrentPage);
        }
    }

    /**
     * For pagination. Makes additional requests when the user reaches the end of the data available
     */
    @Override
    public void loadMoreData() {
        mIsLoading = true;
        mCurrentPage++;
        mNetworkRequests.additionalRequest(mCurrentSortOption, mCurrentPage);
    }

    public String getSortOption() {
        return mCurrentSortOption;
    }

    public void setSortOption(String sortOption) {
        mCurrentSortOption = sortOption;
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
        mMovieList = movieList;
        mCurrentPage = currentPage;
        mTotalPages = totalPages;
        mCurrentSortOption = sortOption;
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
        mIsLoading = false;

        if (movieData != null) {
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

    /**
     * AsyncTask to fetch data from the database
     */
    class FavoritesQueryTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            List<Movie> favoritesList = new ArrayList<>();

            Cursor cursor = mContext.getContentResolver()
                    .query(FavoritesDbContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null,
                            null);

            Log.d("doInBackground Cursor", "Number of rows in cursor: " + cursor.getCount());

            try {
                while (cursor.moveToNext()) {
                    favoritesList.add(new Movie(
                            cursor.getInt(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID)),
                            cursor.getString(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_TITLE)),
                            cursor.getString(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_POSTER_PATH)),
                            cursor.getString(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH)),
                            cursor.getString(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_SYNOPSIS)),
                            cursor.getString(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)),
                            cursor.getFloat(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)),
                            cursor.getInt(cursor.getColumnIndex(FavoritesDbContract.MovieEntry.COLUMN_NAME_VOTE_COUNT))
                    ));
                }
            } finally {
                cursor.close();
            }

            return favoritesList;
        }

        @Override
        protected void onPostExecute(List<Movie> favoritesList) {
            mMovieList = favoritesList;
            mMainActivityView.onMovieSuccess(mMovieList);
        }
    }
}
