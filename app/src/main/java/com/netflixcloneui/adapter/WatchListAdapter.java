package com.netflixcloneui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.ui.TvSeriesDetailActivity;

import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {
    private List<Media> media;
    private final OnItemClickListener listener;

    public WatchListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        media.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, media.size());
    }

    @NonNull
    @Override
    public WatchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watchlist, parent, false);
        return new WatchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchListAdapter.ViewHolder holder, int position) {
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
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(media, holder);
            }
        });
    }

    private void openMediaDetail(Context context, Long id, String type) {
        if ("movie".equals(type)) {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("media_id", id); // Truyền ID phim
            context.startActivity(intent); // Khởi chạy Activity
        } else {
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
        ImageView poster, btnDelete;
        TextView title;
        ImageView play;
        SwipeRevealLayout swipeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            container = itemView.findViewById(R.id.container);
            poster = itemView.findViewById(R.id.img_poster);
            title = itemView.findViewById(R.id.tv_title);
            play = itemView.findViewById(R.id.btn_play);
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(Media media, ViewHolder holder);
    }
}
