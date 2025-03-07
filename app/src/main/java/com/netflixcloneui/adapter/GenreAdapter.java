package com.netflixcloneui.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.R;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.utils.GenreDiffUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<Genre> genres;
    private Map<Long, List<Media>> mediaMap;

    public void setGenresForMedia(List<Genre> genres, Map<Long, List<Media>> mediaMap) {
        this.mediaMap = mediaMap;
        Log.d("GenreAdapter", "Media map size: " + (mediaMap != null ? mediaMap.size() : 0));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GenreDiffUtil(this.genres, genres));
        this.genres = genres;
        Log.d("GenreAdapter", "Genres size: " + (genres != null ? genres.size() : 0));

        diffResult.dispatchUpdatesTo(this);
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_genres, parent, false);
        return new GenreAdapter.GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.txtTitle.setText(genre.getName());

        // Lấy danh sách phim tương ứng với thể loại
        List<Media> media = mediaMap.get(genre.getId());
        //Log.d("GenreAdapter", "Media size for genre " + genre.getName() + ": " + (media != null ? media.size() : 0));

        if (holder.mediaAdapter == null) {
            holder.mediaAdapter = new MediaAdapter(media, false);
            holder.rcvItem.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rcvItem.setAdapter(holder.mediaAdapter);
        } else {
            holder.mediaAdapter.setMedia(media);
        }
    }

    @Override
    public int getItemCount() {
        return genres != null ? genres.size() : 0;
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        RecyclerView rcvItem;
        MediaAdapter mediaAdapter; // Giữ lại Adapter

        GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            rcvItem = itemView.findViewById(R.id.rcv_item);
        }
    }
}
