package com.netflixcloneui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Movie;

import java.util.List;

public class ComingSoonAdapter extends RecyclerView.Adapter<ComingSoonAdapter.ComingSoonViewHolder> {

    private List<Movie> movies;

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComingSoonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coming_soon_movies, parent, false);
        return new ComingSoonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComingSoonViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Load hình ảnh poster
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        holder.textTitle.setText(movie.getTitle());
        holder.textReleaseDate.setText("Ra mắt vào ngày 30 tháng 8 (chưa có dữ liệu)");
        holder.textOverview.setText(movie.getOverview());

        // Xử lý sự kiện khi bấm vào nút "Nhắc tôi"
        holder.buttonNotification.setOnClickListener(v -> {
            // Xử lý logic khi nhấn vào "Nhắc tôi"
        });
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public static class ComingSoonViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView textTitle, textReleaseDate, textOverview;
        Button buttonNotification;

        public ComingSoonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            textTitle = itemView.findViewById(R.id.text_title);
            textReleaseDate = itemView.findViewById(R.id.text_release_date);
            textOverview = itemView.findViewById(R.id.text_overview);
            buttonNotification = itemView.findViewById(R.id.button_notification);
        }
    }
}
