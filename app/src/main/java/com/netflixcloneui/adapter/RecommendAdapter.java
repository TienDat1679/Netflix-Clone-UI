package com.netflixcloneui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.screen.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.screen.TvSeriesDetailActivity;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private List<Media> media;

    public void setMedia(List<Media> media) {
        this.media = media;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new RecommendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.ViewHolder holder, int position) {
        Media media = this.media.get(position);

        // Load hình ảnh poster
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/original" + media.getBackdropPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.poster);

        holder.title.setText(media.getTitle());

        holder.play.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Bạn đã nhấn vào Play", Toast.LENGTH_SHORT).show();
        });
        holder.container.setOnClickListener(v -> {
            Context context = holder.itemView.getContext(); // Lấy Context từ View
            openMediaDetail(context, media.getId(),media.getType());
            Toast.makeText(holder.itemView.getContext(), "Bạn đã chọn: " + media.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void openMediaDetail(Context context, Long id,String type) {
        if("movie".equals(type))
        {

            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("media_id", id); // Truyền ID phim
            context.startActivity(intent); // Khởi chạy Activity
        }
        else {
            Intent intent = new Intent(context, TvSeriesDetailActivity.class);
            intent.putExtra("media_id", id); // Truyền ID phim
            context.startActivity(intent); // Khởi chạy Activity
        }
    }

    @Override
    public int getItemCount() {
        return media != null ? media.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView poster;
        TextView title;
        ImageView play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            poster = itemView.findViewById(R.id.img_poster);
            title = itemView.findViewById(R.id.tv_title);
            play = itemView.findViewById(R.id.btn_play);
        }
    }
}
