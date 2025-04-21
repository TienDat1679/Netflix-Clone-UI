package com.netflixcloneui.adapter;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;
import com.netflixcloneui.ui.TvSeriesDetailActivity;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.util.List;

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.HotViewHolder> {
    private List<Media> media;
    private final int viewType;
    private final UserViewModel userViewModel;
    private final OnMediaClickListener listener;
    public static final int TYPE_HOT = 0;
    public static final int TYPE_TOP_10 = 1;

    public HotAdapter(int viewType, UserViewModel userViewModel, OnMediaClickListener listener) {
        this.viewType = viewType;
        this.userViewModel = userViewModel;
        this.listener = listener;
    }

    public void setMedia(List<Media> hotMedia) {
        this.media = hotMedia;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public HotAdapter.HotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HOT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_movies, parent, false);
        return new HotAdapter.HotViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull HotAdapter.HotViewHolder holder, int position) {
        Media movie = this.media.get(position);

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        holder.textTitle.setText(movie.getTitle());
        holder.textOverview.setText(movie.getOverview());

        holder.linearLayout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMediaDetailClick(movie);
            }
        });

        holder.buttonPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClick(movie);
            }
        });

        holder.buttonAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(movie, holder);
            }
        });

        if (userViewModel.getWatchList().getValue() != null &&
                userViewModel.getWatchList().getValue().contains(movie)) {
            holder.buttonAdd.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_added));
        }

        if (viewType == TYPE_TOP_10) {
            int number = position + 1;
            holder.imgNumberFirst.setImageResource(getNumberResource(number / 10));
            holder.imgNumberSecond.setImageResource(getNumberResource(number % 10));
        }
    }

    @Override
    public int getItemCount() {
        return media != null ? media.size() : 0;
    }

    private int getNumberResource(int number) {
        switch (number) {
            case 0: return R.drawable.ic_no0;
            case 1: return R.drawable.ic_no1;
            case 2: return R.drawable.ic_no2;
            case 3: return R.drawable.ic_no3;
            case 4: return R.drawable.ic_no4;
            case 5: return R.drawable.ic_no5;
            case 6: return R.drawable.ic_no6;
            case 7: return R.drawable.ic_no7;
            case 8: return R.drawable.ic_no8;
            case 9: return R.drawable.ic_no9;
            default: return 0;
        }
    }

    public static class HotViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem, imgNumberFirst, imgNumberSecond;
        TextView textTitle, textOverview;
        MaterialButton buttonPlay;
        public MaterialButton buttonAdd;
        LinearLayout linearLayout;

        public HotViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            textTitle = itemView.findViewById(R.id.text_title);
            textOverview = itemView.findViewById(R.id.text_overview);
            buttonAdd = itemView.findViewById(R.id.button_add);
            buttonPlay = itemView.findViewById(R.id.button_play);
            linearLayout = itemView.findViewById(R.id.linear_layout);

            if (viewType == TYPE_TOP_10) {
                imgNumberFirst = itemView.findViewById(R.id.img_number_first);
                imgNumberSecond = itemView.findViewById(R.id.img_number_second);
            }
        }
    }

    public interface OnMediaClickListener {
        void onPlayClick(Media media);
        void onAddClick(Media media, HotAdapter.HotViewHolder holder);
        void onMediaDetailClick(Media media);
    }
}
