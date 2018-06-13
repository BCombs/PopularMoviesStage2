/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.MovieReview;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private final DetailActivity.OnReviewClickListener mListener;
    private List<MovieReview> mMovieReviews;

    ReviewListAdapter(DetailActivity.OnReviewClickListener listener) {
        this.mListener = listener;
        this.mMovieReviews = null;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieReview review = mMovieReviews.get(position);
        holder.bind(review, mListener);
    }

    @Override
    public int getItemCount() {
        return mMovieReviews == null ? 0 : mMovieReviews.size();
    }

    public void setReviewList(List<MovieReview> movieReviews) {
        this.mMovieReviews = movieReviews;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mAuthorTv;
        private final TextView mContentTv;

        ViewHolder(View itemView) {
            super(itemView);

            mAuthorTv = itemView.findViewById(R.id.author_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

        void bind(final MovieReview review,
                  final DetailActivity.OnReviewClickListener listener) {
            mAuthorTv.setText(review.getAuthor());
            mContentTv.setText(review.getContent());

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = review.getUrl();
                    listener.onItemClick(url);
                }
            });
        }
    }
}
