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
import android.widget.TextView;

import com.billcombsdevelopment.popularmovies.R;
import com.billcombsdevelopment.popularmovies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {

    private final Context mContext;
    private final DetailActivity.OnTrailerClickListener mListener;
    private List<MovieTrailer> mTrailerList;

    public TrailerListAdapter(Context context, DetailActivity.OnTrailerClickListener listener) {
        this.mContext = context;
        this.mTrailerList = null;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieTrailer trailer = mTrailerList.get(position);
        holder.bind(trailer, mListener);
    }

    @Override
    public int getItemCount() {
        return mTrailerList == null ? 0 : mTrailerList.size();
    }

    public void setTrailerList(List<MovieTrailer> trailerList) {
        this.mTrailerList = trailerList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mTrailerIv;
        private final TextView mTrailerTitleTv;

        ViewHolder(View itemView) {
            super(itemView);

            mTrailerIv = itemView.findViewById(R.id.trailer_thumbnail_iv);
            mTrailerTitleTv = itemView.findViewById(R.id.trailer_title_tv);
        }

        void bind(final MovieTrailer trailer,
                  final DetailActivity.OnTrailerClickListener listener) {
            String BASE_THUMBNAIL_URL = "https://img.youtube.com/vi/";
            String DEFAULT_THUMBNAIL_IMAGE = "/1.jpg";

            String thumbnailUrl = BASE_THUMBNAIL_URL + trailer.getKey() + DEFAULT_THUMBNAIL_IMAGE;

            Picasso.with(mContext).load(thumbnailUrl)
                    .placeholder(R.drawable.film)
                    .error(R.drawable.film)
                    .into(mTrailerIv);

            mTrailerTitleTv.setText(trailer.getName());

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(trailer);
                }
            });
        }
    }
}
