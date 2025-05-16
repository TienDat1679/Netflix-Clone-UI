package com.netflixcloneui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Movie;

import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.MovieViewHolder> {
    private List<Movie> movieList;

    public MovieDetailAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_detail, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        String imageUrl = "https://image.tmdb.org/t/p/w500";
        Movie movie = movieList.get(position);

        DisplayMetrics displayMetrics = holder.itemView.getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Đặt chiều rộng của item là 1/3 màn hình
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = screenWidth / 3;
        holder.itemView.setLayoutParams(params);
        // Load hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(imageUrl + movie.getPosterPath())
                .placeholder(R.drawable.load_image)
                .error(R.drawable.error_image)
                .into(holder.imageView);

        // Gán tiêu đề phim
        holder.textView.setText(movie.getTitle());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("media_id", movie.getId()); // Truyền ID phim vào Intent
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageMovie);
            textView = itemView.findViewById(R.id.textTitle);
        }
    }

    public class OnMovieClickListener {
    }
}