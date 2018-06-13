/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.ContentResolver;
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
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.helper.MainActivityHelper;
import com.billcombsdevelopment.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesContract.View {

    private GridLayoutManager mGridLayoutManager;
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mRecyclerAdapter;
    private MainActivityHelper mHelper;
    private Parcelable mLayoutManagerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        initHelper();
        initActionBar();
        initRecyclerView();

        if (savedInstanceState != null) {

            ArrayList<Movie> movieList = savedInstanceState.getParcelableArrayList("movieList");
            int totalPages = savedInstanceState.getInt("totalPages");
            int currentPage = savedInstanceState.getInt("currentPage");
            String sortOption = savedInstanceState.getString("sortOption");
            mLayoutManagerState = savedInstanceState.getParcelable("layoutManagerState");

            mHelper.onRestore(movieList, currentPage, totalPages, sortOption);

            mRecyclerAdapter.setMovieList(movieList);
            mRecyclerAdapter.notifyDataSetChanged();
            mGridLayoutManager.onRestoreInstanceState(mLayoutManagerState);
        } else {
            mHelper.loadMovieData(mHelper.getSortOption());
        }
    }

    /**
     * Initializes the SupportActionBar and the Spinner contained in it
     */
    private void initActionBar() {
        // ActionBar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }

        // Spinner setup
        mSpinner = findViewById(R.id.main_spinner);

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
                if (sortOption.equals(mHelper.getSortOption())) {
                    return;
                }
                mHelper.loadMovieData(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Initializes the activity helper class and registers its observer
     */
    private void initHelper() {
        // Initialize helper
        ContentResolver resolver = getContentResolver();
        mHelper = new MainActivityHelper(this, resolver);
        // Set to default sorting option
        mHelper.setSortOption(getResources().getString(R.string.most_popular));

        /*
         * Registering observer in onCreate() and unregistering in onDestroy() so we can still
         * detect changes to the database when MainActivity is in onPause()
         */
        mHelper.registerObserver();
    }

    /**
     * Initializes all components related to the RecyclerView displaying movie posters
     */
    private void initRecyclerView() {
        // define poster width in pixels
        int posterWidth = 500;
        mGridLayoutManager = new GridLayoutManager(this, calculateSpanCount(posterWidth));

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

                if (!mHelper.isLoading() && !mHelper.isLastPage()) {
                    if ((visibleItemCount + firstVisibleItemPos) >= totalItemCount
                            && firstVisibleItemPos >= 0
                            && totalItemCount >= mHelper.getPageSize()) {
                        mHelper.loadMoreData();
                    }
                }
            }
        });
    }

    /**
     * @param posterWidth - Width in pixels of the poster
     * @return the int to set the span count of the GridLayoutManager
     */
    private int calculateSpanCount(int posterWidth) {
        // Information about display (size, density, ...)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Get the absolute width of display in pixels
        float screenWidth = metrics.widthPixels;

        return Math.round(screenWidth / posterWidth);
    }

    /**
     * Callback for initial movie data load
     *
     * @param movieList
     */
    @Override
    public void onMovieSuccess(List<Movie> movieList) {
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
        super.onPause();
        mLayoutManagerState = mGridLayoutManager.onSaveInstanceState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.unregisterObserver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movieList", mHelper.getMovieList());
        outState.putInt("totalPages", mHelper.getTotalPages());
        outState.putInt("currentPage", mHelper.getCurrentPage());
        outState.putString("sortOption", mHelper.getSortOption());
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
