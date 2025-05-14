package com.netflixcloneui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.util.List;

public class ComingSoonAdapter extends RecyclerView.Adapter<ComingSoonAdapter.ComingSoonViewHolder> {
    private List<Media> movieSeries;
    private final OnMediaClickListener listener;

    public void setMedia(List<Media> movieSeries) {
        this.movieSeries = movieSeries;
        notifyDataSetChanged();
    }

    public ComingSoonAdapter(OnMediaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComingSoonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coming_soon_movies, parent, false);
        return new ComingSoonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComingSoonViewHolder holder, int position) {
        Media movie = this.movieSeries.get(position);

        // Load hình ảnh poster
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        holder.textTitle.setText(movie.getTitle());
        holder.textReleaseDate.setText("Ra mắt vào  " + movie.getReleaseDate());
        holder.textOverview.setText(movie.getOverview());

        if (holder.itemView.getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null) == null) {
            holder.buttonNotification.setVisibility(View.GONE);
        }

        if (movie.isRemind()) {
            holder.buttonNotification.setText("Đã đặt lời nhắc");
            holder.buttonNotification.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_added));
            holder.buttonNotification.setIconTintResource(R.color.black);
        }

        // Xử lý sự kiện khi bấm vào nút "Nhắc tôi"
        holder.buttonNotification.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemindClick(movie, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieSeries != null ? movieSeries.size() : 0;
    }

    public static class ComingSoonViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView textTitle, textReleaseDate, textOverview;
        public MaterialButton buttonNotification;

        public ComingSoonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            textTitle = itemView.findViewById(R.id.text_title);
            textReleaseDate = itemView.findViewById(R.id.text_release_date);
            textOverview = itemView.findViewById(R.id.text_overview);
            buttonNotification = itemView.findViewById(R.id.button_notification);
        }
    }

    public interface OnMediaClickListener {
        void onRemindClick(Media media, int position, ComingSoonViewHolder holder);
    }
}
