/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.presenter.DetailActivityPresenter;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView mBackgroundPosterIv;
    private ImageView mMainPosterIv;
    private ImageButton mFavoritesBtn;
    private TextView mMovieTitleTv;
    private TextView mReleaseYearTv;
    private TextView mSynopsisTv;
    private TextView mTotalRatingsTv;
    private RatingBar mRatingBar;
    private DetailActivityPresenter mPresenter;
    private Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Toolbar setup
        mToolbar = findViewById(R.id.detail_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mBackgroundPosterIv = findViewById(R.id.backround_poster_iv);
        mMainPosterIv = findViewById(R.id.main_poster_iv);
        mMovieTitleTv = findViewById(R.id.movie_title_tv);
        mReleaseYearTv = findViewById(R.id.release_year_tv);
        mSynopsisTv = findViewById(R.id.movie_synopsis_tv);
        mRatingBar = findViewById(R.id.rating_bar);
        mTotalRatingsTv = findViewById(R.id.total_ratings_tv);
        mFavoritesBtn = findViewById(R.id.favorites_btn);

        Movie movie = getIntent().getParcelableExtra("movie");
        mPresenter = new DetailActivityPresenter(movie);

        // Set the toolbar title to the movie title
        getSupportActionBar().setTitle(mPresenter.getMovieTitle());

        mFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter.isFavorite()) {
                    mPresenter.removeFromFavorites();
                    mFavoritesBtn.setImageResource(R.drawable.heart_off);
                    mFavoritesBtn.setAlpha(0.3f);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    String message = mPresenter.getMovieTitle() + " " +
                            getResources().getString(R.string.removed_from_favorites);
                    mToast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();

                } else {
                    mPresenter.setAsFavorite();
                    mFavoritesBtn.setImageResource(R.drawable.heart_clicked);
                    mFavoritesBtn.setAlpha(1.0f);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    String message = mPresenter.getMovieTitle() + " " +
                            getResources().getString(R.string.added_to_favorites);
                    mToast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load the backdrop poster
        Picasso.with(this).load(mPresenter.getBackdropUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .into(mBackgroundPosterIv);

        // Load the main movie poster image
        Picasso.with(this).load(mPresenter.getPosterUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .into(mMainPosterIv);

        mMovieTitleTv.setText(mPresenter.getMovieTitle());
        mReleaseYearTv.setText(mPresenter.getReleaseYear());
        mSynopsisTv.setText(mPresenter.getMovieSynopsis());
        mRatingBar.setRating(mPresenter.getMovieRating());
        mTotalRatingsTv.setText(mPresenter.getTotalRatings(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
