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

    private final Toast mToast = null;
    private DetailActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }

        ImageView backgroundPosterIv = findViewById(R.id.background_poster_iv);
        ImageView mainPosterIv = findViewById(R.id.main_poster_iv);
        TextView movieTitleTv = findViewById(R.id.movie_title_tv);
        TextView releaseYearTv = findViewById(R.id.release_year_tv);
        TextView synopsisTv = findViewById(R.id.movie_synopsis_tv);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        TextView totalRatingsTv = findViewById(R.id.total_ratings_tv);
        final ImageButton favoritesBtn = findViewById(R.id.favorites_btn);

        Movie movie = getIntent().getParcelableExtra("movie");
        mPresenter = new DetailActivityPresenter(movie);

        if (getSupportActionBar() != null) {
            // Set the toolbar title to the movie title
            getSupportActionBar().setTitle(mPresenter.getMovieTitle());
        }

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter.isFavorite()) {
                    mPresenter.removeFromFavorites();
                    favoritesBtn.setImageResource(R.drawable.heart_off);
                    favoritesBtn.setAlpha(0.3f);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    String message = mPresenter.getMovieTitle() + " " +
                            getResources().getString(R.string.removed_from_favorites);
                    mToast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();

                } else {
                    mPresenter.setAsFavorite();
                    favoritesBtn.setImageResource(R.drawable.heart_clicked);
                    favoritesBtn.setAlpha(1.0f);
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
                .into(backgroundPosterIv);

        // Load the main movie poster image
        Picasso.with(this).load(mPresenter.getPosterUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .into(mainPosterIv);

        movieTitleTv.setText(mPresenter.getMovieTitle());
        releaseYearTv.setText(mPresenter.getReleaseYear());
        synopsisTv.setText(mPresenter.getMovieSynopsis());
        ratingBar.setRating(mPresenter.getMovieRating());
        totalRatingsTv.setText(mPresenter.getTotalRatings(this));
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
