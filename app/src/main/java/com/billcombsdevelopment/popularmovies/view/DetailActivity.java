/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
import com.billcombsdevelopment.popularmovies.model.MovieReview;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.billcombsdevelopment.popularmovies.presenter.DetailActivityPresenter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements PopularMoviesContract.DetailView {

    private final Toast mToast = null;
    private DetailActivityPresenter mPresenter;
    private RecyclerView mTrailerRv;
    private RecyclerView mReviewRv;
    private View mTrailerDivider;
    private View mReviewDivider;
    private TextView mTrailerTeaserHeaderTv;
    private TextView mReviewHeaderTv;

    private ReviewListAdapter mReviewAdapter;
    private TrailerListAdapter mTrailerAdapter;

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

        mTrailerDivider = findViewById(R.id.trailer_divider);
        mReviewDivider = findViewById(R.id.review_divider);
        mTrailerTeaserHeaderTv = findViewById(R.id.trailer_teaser_header_tv);
        mReviewHeaderTv = findViewById(R.id.review_header_tv);

        Movie movie = getIntent().getParcelableExtra("movie");
        mPresenter = new DetailActivityPresenter(movie, this);

        // Load movie trailers
        mPresenter.loadTrailerData(mPresenter.getMovieId());
        mPresenter.loadReviewData(mPresenter.getMovieId());

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

        // Setup the trailer RecyclerView
        mTrailerRv = findViewById(R.id.trailer_rv);
        LinearLayoutManager trailerManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mTrailerRv.setLayoutManager(trailerManager);

        mTrailerAdapter = new TrailerListAdapter(this, new OnTrailerClickListener() {
            @Override
            public void onItemClick(MovieTrailer trailer) {
                /*
                 * Picking app or browser from
                 * https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                 */

                Intent youTubeAppIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("vnd.youtube:" + trailer.getKey()));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));

                // Try to open in YouTube app first, if not available open in browser
                try {
                    getApplicationContext().startActivity(youTubeAppIntent);
                } catch (ActivityNotFoundException e) {
                    getApplicationContext().startActivity(browserIntent);
                }
            }
        });
        mTrailerRv.setAdapter(mTrailerAdapter);

        // Set up the review RecyclerView
        mReviewRv = findViewById(R.id.review_rv);
        mReviewAdapter = new ReviewListAdapter(new OnReviewClickListener() {
            @Override
            public void onItemClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                getApplicationContext().startActivity(intent);
            }
        });
        mReviewRv.setAdapter(mReviewAdapter);
        mReviewRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewRv.setHasFixedSize(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mReviewRv);
    }

    public void removeTrailerSection() {
        mTrailerDivider.setVisibility(View.GONE);
        mTrailerTeaserHeaderTv.setVisibility(View.GONE);
        mTrailerRv.setVisibility(View.GONE);
    }

    public void removeReviewSection() {
        mReviewDivider.setVisibility(View.GONE);
        mReviewHeaderTv.setVisibility(View.GONE);
        mReviewRv.setVisibility(View.GONE);
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

    @Override
    public void onTrailerSuccess(List<MovieTrailer> trailerList) {
        // There were no trailers. Inform the user
        if (trailerList.isEmpty()) {
            removeTrailerSection();
            return;
        }

        mTrailerAdapter.setTrailerList(trailerList);
        mTrailerRv.setAdapter(mTrailerAdapter);
    }

    @Override
    public void onReviewSuccess(List<MovieReview> movieReviews) {
        // There were no reviews. Inform the user
        if (movieReviews.isEmpty()) {
            removeReviewSection();
            return;
        }

        mReviewAdapter.setReviewList(movieReviews);
        mReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {
        mToast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }

    public interface OnTrailerClickListener {
        void onItemClick(MovieTrailer trailer);
    }

    public interface OnReviewClickListener {
        void onItemClick(String url);
    }
}
