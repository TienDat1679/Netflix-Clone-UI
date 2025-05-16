package com.netflixcloneui.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.WatchListAdapter;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.utils.SwipeToDeleteCallback;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class WatchListActivity extends AppCompatActivity {
    ImageView btnBack;
    RecyclerView rcvWatchList;
    WatchListAdapter watchListAdapter;
    MaterialButton btnSeries, btnMovies, btnReleased, btnNotReleased;
    MediaRepository mediaRepository;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(getApplicationContext());
            }
        }).get(UserViewModel.class);
        rcvWatchList = findViewById(R.id.rcv_watch_list);
        btnBack = findViewById(R.id.img_back);
        btnSeries = findViewById(R.id.btn_series);
        btnMovies = findViewById(R.id.btn_movies);
        btnReleased = findViewById(R.id.btn_released);
        btnNotReleased = findViewById(R.id.btn_not_released);
        mediaRepository = new MediaRepository(this);
        watchListAdapter = new WatchListAdapter(new WatchListAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Media media, WatchListAdapter.ViewHolder holder) {
                userViewModel.getUserId().observe(WatchListActivity.this, userId -> {
                    if (userId != null) {
                        mediaRepository.removeMediaFromWatchList(userId, media.getId());
                    }
                });
                watchListAdapter.deleteItem(holder.getBindingAdapterPosition());
            }
        });

        List<Media> mediaList = (List<Media>) getIntent().getSerializableExtra("media_list");
        rcvWatchList.setLayoutManager(new LinearLayoutManager(this));
        rcvWatchList.setAdapter(watchListAdapter);
        watchListAdapter.setMedia(mediaList);

        // Kết nối swipe-to-delete
//        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(watchListAdapter, this);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
//        itemTouchHelper.attachToRecyclerView(rcvWatchList);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSeries.setOnClickListener(v -> {
            List<Media> series = mediaList.stream()
                    .filter(media -> "tv_series".equals(media.getType()))
                    .collect(Collectors.toList());
            watchListAdapter.setMedia(series);
        });

        btnMovies.setOnClickListener(v -> {
            List<Media> movies = mediaList.stream()
                    .filter(media -> "movie".equals(media.getType()))
                    .collect(Collectors.toList());
            watchListAdapter.setMedia(movies);
        });

        btnReleased.setOnClickListener(v -> {
            List<Media> released = mediaList.stream()
                    .filter(Media::isReleased)
                    .collect(Collectors.toList());
            watchListAdapter.setMedia(released);
        });

        btnNotReleased.setOnClickListener(v -> {
            List<Media> notReleased = mediaList.stream()
                    .filter(media -> !media.isReleased())
                    .collect(Collectors.toList());
            watchListAdapter.setMedia(notReleased);
        });
    }
}