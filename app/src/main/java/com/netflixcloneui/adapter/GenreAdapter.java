package com.netflixcloneui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.R;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Movie;

import java.util.List;
import java.util.Map;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<Genre> genres;
    private Map<Long, List<Movie>> moviesMap;
    private MovieAdapter.OnMovieClickListener movieClickListener;

    public GenreAdapter(MovieAdapter.OnMovieClickListener listener) {
        this.movieClickListener = listener;
    }

    public void setGenres(List<Genre> genres, Map<Long, List<Movie>> moviesMap) {
        this.genres = genres;
        this.moviesMap = moviesMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_genres, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.txtTitle.setText(genre.getName());

        // Lấy danh sách phim tương ứng với thể loại
        List<Movie> movies = moviesMap != null ? moviesMap.get(genre.getId()) : null;
        MovieAdapter movieAdapter = new MovieAdapter(movies, false, movieClickListener);
        holder.rcvItem.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.rcvItem.setAdapter(movieAdapter); // Hiển thị danh sách phim
    }

    @Override
    public int getItemCount() {
        return genres != null ? genres.size() : 0;
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        RecyclerView rcvItem;

        GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            rcvItem = itemView.findViewById(R.id.rcv_item);
        }
    }
}
