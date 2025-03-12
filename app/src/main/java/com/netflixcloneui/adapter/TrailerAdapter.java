package com.netflixcloneui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Trailer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> trailerList;
    private LifecycleOwner lifecycleOwner;

    public TrailerAdapter(List<Trailer> trailerList, LifecycleOwner lifecycleOwner) {
        this.trailerList = trailerList;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        holder.trailerName.setText(trailer.getName());

        // Đăng ký LifecycleOwner để tránh lỗi lifecycle
        lifecycleOwner.getLifecycle().addObserver(holder.youTubePlayerView);

        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(trailer.getKey(), 0);
            }
        });
    }


    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        TextView trailerName;
        YouTubePlayerView youTubePlayerView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_name);
            youTubePlayerView = itemView.findViewById(R.id.youtubePlayerView);
        }
    }
}