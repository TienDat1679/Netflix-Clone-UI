package com.netflixcloneui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Media> mediaList;

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        Media media = mediaList.get(position);

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/original" + media.getBackdropPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.ivPoster);

        holder.tvTitle.setText(media.getTitle());
        holder.tvReleaseDate.setText("Ra mắt vào " + media.getReleaseDate());
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvReleaseDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
        }
    }
}
