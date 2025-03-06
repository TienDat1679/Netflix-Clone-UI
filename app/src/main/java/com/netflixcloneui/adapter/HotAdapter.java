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
import com.google.android.material.card.MaterialCardView;
import com.netflixcloneui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;

import java.util.List;

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.HotViewHolder> {
    private List<Movie> movies;
    private List<TVSeries> series;
    private List<Media> hot;
    private final int viewType;
    public static final int TYPE_HOT = 0;
    public static final int TYPE_TOP_MOVIES = 1;
    public static final int TYPE_TOP_SERIES = 2;

    public HotAdapter(int viewType) {
        this.viewType = viewType;
    }

    public void setHotMedia(List<Media> hotMedia) {
        this.hot = hotMedia;
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movieSeries) {
        this.movies = movieSeries;
        notifyDataSetChanged();
    }

    public void setSeries(List<TVSeries> series) {
        this.series = series;
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
        if (viewType == TYPE_HOT) {
            Media movie = this.hot.get(position);
            // Load hình ảnh poster
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                    .placeholder(R.drawable.ic_info)
                    .into(holder.imgItem);

            holder.textTitle.setText(movie.getTitle());
            holder.textOverview.setText(movie.getOverview());
            holder.linearLayout.setOnClickListener(v -> {
                openMediaDetail(v.getContext(), movie.getId());
                Toast.makeText(v.getContext(), "Bạn đã nhấn vào: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            });
        } else if (viewType == TYPE_TOP_MOVIES) {
            Movie movie = this.movies.get(position);
            int number = position + 1;
            holder.imgNumberFirst.setImageResource(getNumberResource(number / 10));
            holder.imgNumberSecond.setImageResource(getNumberResource(number % 10));
            // Load hình ảnh poster
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                    .placeholder(R.drawable.ic_info)
                    .into(holder.imgItem);

            holder.textTitle.setText(movie.getTitle());
            holder.textOverview.setText(movie.getOverview());
            holder.linearLayout.setOnClickListener(v -> {
                openMediaDetail(v.getContext(), movie.getId());
                Toast.makeText(v.getContext(), "Bạn đã nhấn vào: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            });
        } else {
            TVSeries movie = this.series.get(position);
            int number = position + 1;
            holder.imgNumberFirst.setImageResource(getNumberResource(number / 10));
            holder.imgNumberSecond.setImageResource(getNumberResource(number % 10));
            // Load hình ảnh poster
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                    .placeholder(R.drawable.ic_info)
                    .into(holder.imgItem);

            holder.textTitle.setText(movie.getName());
            holder.textOverview.setText(movie.getOverview());
            holder.linearLayout.setOnClickListener(v -> {
                openMediaDetail(v.getContext(), movie.getId());
                Toast.makeText(v.getContext(), "Bạn đã nhấn vào: " + movie.getName(), Toast.LENGTH_SHORT).show();
            });
        }

//        Movie movie = this.movies.get(position);
//
//        // Load hình ảnh poster
//        Glide.with(holder.itemView.getContext())
//                .load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
//                .placeholder(R.drawable.ic_info)
//                .into(holder.imgItem);
//
//        holder.textTitle.setText(movie.getTitle());
//        holder.textOverview.setText(movie.getOverview());
//
//        // Nếu là TOP_MOVIES, hiển thị số thứ tự từ 1 đến 10
//        if (viewType == TYPE_TOP_MOVIES || viewType == TYPE_TOP_SERIES) {
//            holder.imgNumberFirst.setVisibility(View.VISIBLE);
//            holder.imgNumberSecond.setVisibility(View.VISIBLE);
//
//            int number = position + 1;
//            holder.imgNumberFirst.setImageResource(getNumberResource(number / 10));
//            holder.imgNumberSecond.setImageResource(getNumberResource(number % 10));
//        } else {
//            if (holder.imgNumberFirst != null) holder.imgNumberFirst.setVisibility(View.GONE);
//            if (holder.imgNumberSecond != null) holder.imgNumberSecond.setVisibility(View.GONE);
//        }

        // Xử lý sự kiện khi bấm vào nút
        holder.buttonPlay.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Bạn đã nhấn vào Play", Toast.LENGTH_SHORT).show();
        });
        holder.buttonAdd.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Bạn đã nhấn vào Them vao danh sach", Toast.LENGTH_SHORT).show();
        });
    }

    private void openMediaDetail(Context context, Long id) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("movie_id", id); // Truyền ID phim
        context.startActivity(intent); // Khởi chạy Activity
    }

    @Override
    public int getItemCount() {
        if (viewType == TYPE_HOT) return hot != null ? hot.size() : 0;
        else if (viewType == TYPE_TOP_MOVIES) return movies != null ? movies.size() : 0;
        else return series != null ? series.size() : 0;
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
        Button buttonPlay, buttonAdd;
        LinearLayout linearLayout;

        public HotViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            textTitle = itemView.findViewById(R.id.text_title);
            textOverview = itemView.findViewById(R.id.text_overview);
            buttonAdd = itemView.findViewById(R.id.button_add);
            buttonPlay = itemView.findViewById(R.id.button_play);
            linearLayout = itemView.findViewById(R.id.linear_layout);

            if (viewType == TYPE_TOP_MOVIES || viewType == TYPE_TOP_SERIES) {
                imgNumberFirst = itemView.findViewById(R.id.img_number_first);
                imgNumberSecond = itemView.findViewById(R.id.img_number_second);
            }
        }
    }
}
