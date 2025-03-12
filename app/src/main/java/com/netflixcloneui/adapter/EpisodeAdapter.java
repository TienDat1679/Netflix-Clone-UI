package com.netflixcloneui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Episode;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private Context context;
    private List<Episode> episodeList;
    private OnEpisodeClickListener listener;
    String imageUrl = "https://image.tmdb.org/t/p/w500";

    public interface OnEpisodeClickListener {
        void onEpisodeClick(String videoKey);
    }
    public EpisodeAdapter(Context context, List<Episode> episodeList,OnEpisodeClickListener listener) {
        this.context = context;
        this.episodeList = episodeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.eps_item, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        holder.tvEpisodeTitle.setText("Tập " + episode.getEpisodeNumber() + ": " + episode.getName());
        holder.tvEpisodeDuration.setText(episode.getRuntime() + " phút");
        holder.tvEpisodeDescription.setText(episode.getOverview());

        // Load hình ảnh bằng Glide

        Glide.with(context)
                .load(imageUrl+ episode.getStillPath())
                .placeholder(R.drawable.load_image)
                .into(holder.imgThumbnail);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEpisodeClick("xbsT5l4hdfA"); // Gửi videoKey của tập phim được chọn
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail, imgPlayIcon, imgDownload;
        TextView tvEpisodeTitle, tvEpisodeDuration, tvEpisodeDescription;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgPlayIcon = itemView.findViewById(R.id.imgPlayIcon);
            imgDownload = itemView.findViewById(R.id.imgDownload);
            tvEpisodeTitle = itemView.findViewById(R.id.tvEpisodeTitle);
            tvEpisodeDuration = itemView.findViewById(R.id.tvEpisodeDuration);
            tvEpisodeDescription = itemView.findViewById(R.id.tvEpisodeDescription);
        }
    }
}
