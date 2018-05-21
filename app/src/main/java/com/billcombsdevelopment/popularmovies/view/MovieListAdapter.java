/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private List<Movie> mMovieList;
    private Context mContext;
    private MainActivity.OnItemClickListener mListener;
    private String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    public MovieListAdapter(Context context, List<Movie> movieList,
                              MainActivity.OnItemClickListener listener) {
        this.mMovieList = movieList;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        holder.bindData(movie, mListener);
    }

    @Override
    public int getItemCount() {
        return mMovieList == null ? 0 : mMovieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPosterImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mPosterImageView = itemView.findViewById(R.id.list_item_iv);
        }

        public void bindData(final Movie movie, final MainActivity.OnItemClickListener listener) {
            String imageUrl = BASE_POSTER_URL + movie.getPosterPath();

            Picasso.with(mContext).load(imageUrl)
                    .placeholder(R.drawable.film)
                    .error(R.drawable.film)
                    .noFade()
                    .into(mPosterImageView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        }
    }
}
