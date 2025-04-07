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
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.ui.TvSeriesDetailActivity;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MovieViewHolder> {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FAVORITE = 1;
    public static final int TYPE_TOP_10 = 2;
    private List<Media> media;
    private final int viewType;

//    private OnMovieClickListener listener;
//    public interface OnMovieClickListener {
//        void onMovieClick(Movie movie);
//        void onSeriesClick(TVSeries series);
//        void onMediaClick(Media media);
//    }

    public MediaAdapter(List<Media> media, int viewType/*, OnMovieClickListener listener*/) {
        this.media = media;
        this.viewType = viewType;
        setHasStableIds(true);
        //this.listener = listener;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FAVORITE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_movies, parent, false);
        } else if (viewType == TYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movies, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_10, parent, false);
        }
        return new MovieViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Media media = this.media.get(position);
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + media.getPosterPath())
                .placeholder(R.drawable.ic_info)
                .into(holder.imgItem);

        if (viewType == TYPE_TOP_10) {
            int number = position + 1;
            holder.imgNumber.setImageResource(getNumberResource(number % 10));
        }

        holder.itemView.setOnClickListener(v -> {
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

    private int getNumberResource(int number) {
        switch (number) {
            case 0: return R.drawable.ic_no10;
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

    @Override
    public int getItemCount() {
        return media != null ? media.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return media.get(position).getId(); // Giả sử ID của Media là duy nhất
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem, imgNumber;

        public MovieViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);

            if (viewType == TYPE_TOP_10) {
                imgNumber = itemView.findViewById(R.id.img_number);
            }
        }
    }
}

