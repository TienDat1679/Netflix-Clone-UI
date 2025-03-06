package com.netflixcloneui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FAVORITE = 1;

    private List<Movie> movieSeries;
    private OnMovieClickListener listener;
    private boolean isFavoriteList; // Biến để xác định danh sách là Favorite hay không

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movieSeries, boolean isFavoriteList, OnMovieClickListener listener) {
        this.movieSeries = movieSeries;
        this.isFavoriteList = isFavoriteList;
        this.listener = listener;
    }

    public void setMovies(List<Movie> movieSeries) {
        this.movieSeries = movieSeries;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return isFavoriteList ? TYPE_FAVORITE : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FAVORITE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_movies, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movies, parent, false);
        }
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = this.movieSeries.get(position);
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieSeries != null ? movieSeries.size() : 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
        }
    }
}

