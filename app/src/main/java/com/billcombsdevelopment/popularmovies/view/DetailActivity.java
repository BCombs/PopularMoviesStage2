/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.billcombsdevelopment.popularmovies.helper.DetailActivityHelper;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.billcombsdevelopment.popularmovies.model.MovieReview;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements PopularMoviesContract.DetailView {

    private final Toast mToast = null;
    private DetailActivityHelper mHelper;
    private RecyclerView mTrailerRv;
    private RecyclerView mReviewRv;
    private View mTrailerDivider;
    private View mReviewDivider;
    private TextView mTrailerTeaserHeaderTv;
    private TextView mReviewHeaderTv;
    private ImageView mBackgroundPosterIv;
    private ImageView mMainPosterIv;
    private TextView mMovieTitleTv;
    private TextView mReleaseYearTv;
    private TextView mSynopsisTv;
    private RatingBar mRatingBar;
    private TextView mTotalRatingsTv;
    private ImageButton mFavoritesBtn;

    private ReviewListAdapter mReviewAdapter;
    private Parcelable mReviewManagerState;
    private TrailerListAdapter mTrailerAdapter;
    private Parcelable mTrailerManagerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // ActionBar setup
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }

        final Movie movie = getIntent().getParcelableExtra("movie");

        // Initialize helper class
        mHelper = new DetailActivityHelper(movie, this, getApplicationContext());

        // Set ActionBar title to the movie title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mHelper.getMovieTitle());
        }

        // Initialize the UI and load data
        initUiElements();
        initTrailerRecyclerView();
        initReviewRecyclerView();
        loadUiData(movie);

        if (savedInstanceState != null) {
            // We already have trailers and reviews, no need to fetch them again
            mTrailerManagerState = savedInstanceState.getParcelable("trailerManagerState");
            mReviewManagerState = savedInstanceState.getParcelable("reviewManagerState");
            List<MovieTrailer> trailerList =
                    savedInstanceState.getParcelableArrayList("trailers");
            List<MovieReview> reviewList =
                    savedInstanceState.getParcelableArrayList("reviews");

            // Set the review and trailer list in helper class
            mHelper.setTrailerList(trailerList);
            mHelper.setReviewList(reviewList);

            // If there are no trailers, remove that section from the UI
            if (trailerList == null || trailerList.isEmpty()) {
                removeTrailerSection();
            } else {
                // Restore Trailer State
                mTrailerAdapter.setTrailerList(trailerList);
                mTrailerAdapter.notifyDataSetChanged();
                mTrailerRv.getLayoutManager().onRestoreInstanceState(mTrailerManagerState);
            }

            // If there are no reviews, remove that section from the UI
            if (reviewList == null || reviewList.isEmpty()) {
                removeReviewSection();
            } else {
                // Restore Review state
                mReviewAdapter.setReviewList(reviewList);
                mReviewAdapter.notifyDataSetChanged();
                mReviewRv.getLayoutManager().onRestoreInstanceState(mReviewManagerState);
            }
        } else {
            // savedInstanceState was null, fetch trailers and reviews
            // Load movie trailers
            mHelper.loadTrailerData(mHelper.getMovieId());

            // Load movie reviews
            mHelper.loadReviewData(mHelper.getMovieId());
        }
    }

    private void initUiElements() {
        mBackgroundPosterIv = findViewById(R.id.background_poster_iv);
        mMainPosterIv = findViewById(R.id.main_poster_iv);
        mMovieTitleTv = findViewById(R.id.movie_title_tv);
        mReleaseYearTv = findViewById(R.id.release_year_tv);
        mSynopsisTv = findViewById(R.id.movie_synopsis_tv);
        mRatingBar = findViewById(R.id.rating_bar);
        mTotalRatingsTv = findViewById(R.id.total_ratings_tv);
        mFavoritesBtn = findViewById(R.id.favorites_btn);
        mTrailerDivider = findViewById(R.id.trailer_divider);
        mReviewDivider = findViewById(R.id.review_divider);
        mTrailerTeaserHeaderTv = findViewById(R.id.trailer_teaser_header_tv);
        mReviewHeaderTv = findViewById(R.id.review_header_tv);
        mTrailerRv = findViewById(R.id.trailer_rv);
        mReviewRv = findViewById(R.id.review_rv);
    }

    /**
     * Loads all of the information from the movie into the UI elements
     *
     * @param movie - Movie object
     */
    private void loadUiData(final Movie movie) {

        // Load the backdrop poster
        Picasso.with(this).load(mHelper.getBackdropUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .into(mBackgroundPosterIv);

        // Load the main movie poster image
        Picasso.with(this).load(mHelper.getPosterUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .into(mMainPosterIv);

        // Favorite button setup
        setIsFavorite(movie.getId());
        mFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHelper.isFavorite()) {
                    /*
                     * The movie is currently a favorite, delete it from the database
                     * and update the drawable
                     */
                    mHelper.deleteFromFavorites(movie.getId());
                    mFavoritesBtn.setImageResource(R.drawable.heart_unclicked);
                    mFavoritesBtn.setAlpha(0.3f);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    // Let the user know we removed it from the database
                    String message = mHelper.getMovieTitle() + " " +
                            getResources().getString(R.string.removed_from_favorites);
                    mToast.makeText(DetailActivity.this, message,
                            Toast.LENGTH_SHORT).show();

                } else {
                    /*
                     * The movie is not a favorite. Add it to the database and
                     * update the drawable
                     */
                    mHelper.setAsFavorite();
                    mFavoritesBtn.setImageResource(R.drawable.heart_clicked);
                    mFavoritesBtn.setAlpha(1.0f);
                    if (mToast != null) {
                        mToast.cancel();
                    }

                    mHelper.addToFavorites(movie);

                    // Let the user know we added it to the database
                    String message = mHelper.getMovieTitle() + " " +
                            getResources().getString(R.string.added_to_favorites);
                    mToast.makeText(DetailActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set TextView data
        mMovieTitleTv.setText(mHelper.getMovieTitle());
        mReleaseYearTv.setText(mHelper.getReleaseYear());
        mSynopsisTv.setText(mHelper.getMovieSynopsis());
        mRatingBar.setRating(mHelper.getMovieRating());
        String ratings = String.format(
                getResources().getString(R.string.total_ratings), mHelper.getTotalRatings());
        mTotalRatingsTv.setText(ratings);
    }

    /**
     * Setup all components related to the movie trailer RecyclerView
     */
    private void initTrailerRecyclerView() {
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
                    DetailActivity.this.startActivity(youTubeAppIntent);
                } catch (ActivityNotFoundException e) {
                    DetailActivity.this.startActivity(browserIntent);
                }
            }
        });

        mTrailerRv.setAdapter(mTrailerAdapter);
        mTrailerRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mTrailerRv.setHasFixedSize(true);
    }

    /**
     * Setup all components related to the review RecyclerView
     */
    private void initReviewRecyclerView() {
        mReviewAdapter = new ReviewListAdapter(new OnReviewClickListener() {
            @Override
            public void onItemClick(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                getApplicationContext().startActivity(intent);
            }
        });
        mReviewRv.setAdapter(mReviewAdapter);
        mReviewRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mReviewRv.setHasFixedSize(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mReviewRv);
    }

    /**
     * Checks to see if the movie is already stored in the favorites database
     * If it is already a favorite, set the heart to clicked, else unclicked
     *
     * @param movieId - (int) the ID of the movie
     */
    private void setIsFavorite(final int movieId) {
        // Check if the movie is a favorite
        boolean queryIsFavorite = mHelper.queryIsFavorite(movieId);
        if (queryIsFavorite) {
            mFavoritesBtn.setImageResource(R.drawable.heart_clicked);
            mFavoritesBtn.setAlpha(1.0f);
        } else {
            mFavoritesBtn.setImageResource(R.drawable.heart_unclicked);
            mFavoritesBtn.setAlpha(0.3f);
        }
    }

    private void removeTrailerSection() {
        mTrailerDivider.setVisibility(View.GONE);
        mTrailerTeaserHeaderTv.setVisibility(View.GONE);
        mTrailerRv.setVisibility(View.GONE);
    }

    private void removeReviewSection() {
        mReviewDivider.setVisibility(View.GONE);
        mReviewHeaderTv.setVisibility(View.GONE);
        mReviewRv.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                    return true;
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("trailers",
                (ArrayList<? extends Parcelable>) mHelper.getTrailerList());
        outState.putParcelableArrayList("reviews",
                (ArrayList<? extends Parcelable>) mHelper.getReviewList());
        outState.putParcelable("trailerManagerState", mTrailerManagerState);
        outState.putParcelable("reviewManagerState", mReviewManagerState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the states of the trailer and review layout managers
        mTrailerManagerState = mTrailerRv.getLayoutManager().onSaveInstanceState();
        mReviewManagerState = mReviewRv.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onTrailerSuccess(List<MovieTrailer> trailerList) {
        // There were no trailers. Remove the trailer section
        if (trailerList.isEmpty()) {
            removeTrailerSection();
            return;
        }

        mTrailerAdapter.setTrailerList(trailerList);
        mTrailerRv.setAdapter(mTrailerAdapter);
    }

    @Override
    public void onReviewSuccess(List<MovieReview> movieReviews) {
        // There were no reviews. Remove the Review section
        if (movieReviews.isEmpty()) {
            removeReviewSection();
            return;
        }

        mReviewAdapter.setReviewList(movieReviews);
        mReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {
        mToast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public interface OnTrailerClickListener {
        void onItemClick(MovieTrailer trailer);
    }

    public interface OnReviewClickListener {
        void onItemClick(String url);
    }
}
