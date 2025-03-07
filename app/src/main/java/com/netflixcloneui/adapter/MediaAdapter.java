package com.netflixcloneui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MovieViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FAVORITE = 1;
    private List<Media> media;
    private boolean isFavoriteList; // Biến để xác định danh sách là Favorite hay không

//    private OnMovieClickListener listener;
//    public interface OnMovieClickListener {
//        void onMovieClick(Movie movie);
//        void onSeriesClick(TVSeries series);
//        void onMediaClick(Media media);
//    }

    public MediaAdapter(List<Media> media, boolean isFavoriteList/*, OnMovieClickListener listener*/) {
        this.media = media;
        this.isFavoriteList = isFavoriteList;
        setHasStableIds(true);
        //this.listener = listener;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
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
        Media media = this.media.get(position);

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + media.getPosterPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext(); // Lấy Context từ View
            openMediaDetail(context, media.getId());
            Toast.makeText(holder.itemView.getContext(), "Bạn đã chọn: " + media.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void openMediaDetail(Context context, Long id) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("movie_id", id); // Truyền ID phim
        context.startActivity(intent); // Khởi chạy Activity
    }

    @Override
    public int getItemCount() {
        return media != null ? media.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return media.get(position).getId(); // Giả sử ID của Media là duy nhất
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
        }
    }
}

