/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.presenter.MainActivityPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesContract.View {

    private final GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mRecyclerAdapter;
    private MainActivityPresenter mPresenter;
    private Parcelable mLayoutManagerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Toolbar and sorting spinner initialization
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        mSpinner = findViewById(R.id.main_spinner);

        // Initialize Presenter
        mPresenter = new MainActivityPresenter(this, getApplicationContext());
        // Set to default sorting option
        mPresenter.setSortOption(getResources().getString(R.string.most_popular));

        // Spinner setup
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                getResources().getStringArray(R.array.sort_options_array));
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sortOption = mSpinner.getSelectedItem().toString();
                // This check is to prevent initial HTTP request on Listener initialization
                if (sortOption.equals(mPresenter.getSortOption())) {
                    return;
                }
                mPresenter.loadMovieData(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // RecyclerView setup
        mRecyclerView = findViewById(R.id.movie_list_rv);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        ArrayList<Movie> movieList = new ArrayList<>();
        mRecyclerAdapter = new MovieListAdapter(this, movieList, new OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie, View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("movie", movie);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(
                                    MainActivity.this,
                                    view,
                                    getResources().getString(R.string.movie_poster_transition));

                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        mRecyclerView.setAdapter(mRecyclerAdapter);

        /* Set an OnScrollListener for pagination
         * Information found at
         * https://medium.com/@etiennelawlor/pagination-with-recyclerview-1cb7e66a502b
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mGridLayoutManager.getChildCount();
                int totalItemCount = mGridLayoutManager.getItemCount();
                int firstVisibleItemPos =
                        mGridLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (!mPresenter.isLoading() && !mPresenter.isLastPage()) {
                    if ((visibleItemCount + firstVisibleItemPos) >= totalItemCount
                            && firstVisibleItemPos >= 0
                            && totalItemCount >= mPresenter.getPageSize()) {
                        mPresenter.loadMoreData();
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList("movieList");
            int totalPages = savedInstanceState.getInt("totalPages");
            int currentPage = savedInstanceState.getInt("currentPage");
            String sortOption = savedInstanceState.getString("sortOption");
            mLayoutManagerState = savedInstanceState.getParcelable("layoutManagerState");

            mPresenter.onRestore(movieList, currentPage, totalPages, sortOption);
            mRecyclerView.invalidate();
            mRecyclerAdapter.setMovieList(movieList);
            mRecyclerAdapter.notifyDataSetChanged();
            mGridLayoutManager.onRestoreInstanceState(mLayoutManagerState);
        } else {
            mPresenter.loadMovieData(mPresenter.getSortOption());
        }
    }

    /**
     * Callback for initial movie data load
     *
     * @param movieList
     */
    @Override
    public void onMovieSuccess(List<Movie> movieList) {
        Log.d("onMovieSuccess", "Movie List Size: " + movieList.size());
        mRecyclerAdapter.setMovieList(movieList);
        mRecyclerView.scrollToPosition(0);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * Callback for subsequent requests for pagination
     *
     * @param movieList
     */
    @Override
    public void onUpdate(List<Movie> movieList) {
        mRecyclerAdapter.setMovieList(movieList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        mLayoutManagerState = mGridLayoutManager.onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter.getSortOption().equals(getResources().getString(R.string.favorites))) {
            String sortOption = mPresenter.getSortOption();
            mPresenter.loadMovieData(sortOption);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movieList", mPresenter.getMovieList());
        outState.putInt("totalPages", mPresenter.getTotalPages());
        outState.putInt("currentPage", mPresenter.getCurrentPage());
        outState.putString("sortOption", mPresenter.getSortOption());
        outState.putParcelable("layoutManagerState", mLayoutManagerState);
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Click listener for RecyclerView
     */
    public interface OnItemClickListener {
        void onItemClick(Movie movie, View view);
    }
}
