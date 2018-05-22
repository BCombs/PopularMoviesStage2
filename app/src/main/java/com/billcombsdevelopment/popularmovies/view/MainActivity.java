/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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

        // Toolbar and sorting spinner setup
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        mSpinner = findViewById(R.id.main_spinner);

        // Initialize Presenter
        mPresenter = new MainActivityPresenter(this);
        // Set to default sorting option
        mPresenter.setSortOption(getResources().getString(R.string.most_popular));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                getResources().getStringArray(R.array.sort_options_array));
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sortOption = mSpinner.getSelectedItem().toString();
                // Check is to prevent initial HTTP request on Listener initialization
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
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
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
                        mPresenter.loadMoreMovieData();
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

    @Override
    public void onSuccess(List<Movie> movieList) {
        Log.d("onSuccess", movieList.get(0).getTitle());
        mRecyclerView.invalidate();
        mRecyclerAdapter.setMovieList(movieList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

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
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }
}
