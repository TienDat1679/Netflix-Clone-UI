package com.netflixcloneui.screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.EpisodeAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.adapter.MovieDetailAdapter;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;
import com.netflixcloneui.model.Trailer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvSeriesDetailActivity extends AppCompatActivity implements EpisodeAdapter.OnEpisodeClickListener{

    private RecyclerView recyclerViewEps;
    private Button btnPlay;
    boolean isExpanded = false;
    private EpisodeAdapter EpsAdapter;
    List<Episode> listEps;
    List <Media> listMedia;
    List<Trailer> listTrailer;
    private YouTubePlayer youTubePlayerInstance;
    private YouTubePlayerView youTubePlayerView;
    private MovieDetailAdapter movieAdapter;

    private SnapHelper snapHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tv_series_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnPlay = findViewById(R.id.btnPlay);
        long id = (long) getIntent().getLongExtra("media_id",-1);
        getTvSeriesDetail(id);
        btnPlay.setOnClickListener(view ->playFullScreenVideo() );
        getEsp(id);
        getTrailer(id);
        getMediaSame(id);
        ChangeRecycle();
    }

    private void playFullScreenVideo() {
        Intent intent = new Intent(this, FullScreenVideoActivity.class);
        intent.putExtra("VIDEO_ID", "xbsT5l4hdfA"); // Truyền videoId vào Intent
        startActivity(intent);
    }

    private void getTrailer(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Trailer> >call = apiService.getSeriesTrailer(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Trailer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Trailer> >call, @NonNull Response<List<Trailer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listTrailer= response.body();

                    youTubePlayerView = findViewById(R.id.youtubePlayer);
                    getLifecycle().addObserver(youTubePlayerView);

                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            youTubePlayerInstance = youTubePlayer;
                            String videoKey = listTrailer.get(0).getKey(); // ID của video YouTube
                            youTubePlayer.loadVideo(videoKey, 0);
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Trailer>> call, @NonNull Throwable t) {
                Log.e("Trailer", "API Call failed: " + t.getMessage());
            }
        });
    }

    private void ChangeRecycle() {
        TextView sameMedia = findViewById(R.id.sameMedia);
        TextView esp = findViewById(R.id.esp);

        sameMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(TvSeriesDetailActivity.this, 3); // 3 cột
                recyclerViewEps.setLayoutManager(gridLayoutManager);
                recyclerViewEps.setAdapter(new MediaAdapter(listMedia,false ));

            }
        });
        esp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewEps.setLayoutManager(new LinearLayoutManager(TvSeriesDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerViewEps.setAdapter(new EpisodeAdapter(TvSeriesDetailActivity.this, listEps,TvSeriesDetailActivity.this::onEpisodeClick));
            }
        });
    }

    private void getMediaSame(long id)
    {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Media> >call = apiService.getSameMedia(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media> >call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listMedia= response.body();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Media>> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }
    private void getEsp(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                listEps = response.body();
                recyclerViewEps = findViewById(R.id.recyclerEpisodes);

                // Thiết lập RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(TvSeriesDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerViewEps.setLayoutManager(layoutManager);
                EpsAdapter = new EpisodeAdapter(TvSeriesDetailActivity.this,listEps,TvSeriesDetailActivity.this::onEpisodeClick);
                recyclerViewEps.setAdapter(EpsAdapter);

                // Dùng SnapHelper để cuộn từng phim một cách mượt mà
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerViewEps);
            }
            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("Tvseries Eps", "API Call failed: " + t.getMessage());
            }
        });
    }
    private void getTvSeriesDetail(Long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<TVSeries> call = apiService.getTvSeriesDetail(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<TVSeries>() {
            @Override
            public void onResponse(@NonNull Call<TVSeries>call, @NonNull Response<TVSeries> response) {
                if (response.isSuccessful() && response.body() != null) {

                    TVSeries series= response.body();
                    TextView tvName= (TextView) findViewById(R.id.tvName);
                    tvName.setText(series.getName());

                    TextView tvOverview=(TextView) findViewById(R.id.tvOverview);
                    tvOverview.setText(series.getOverview());

                    TextView tvInfo= (TextView) findViewById(R.id.tvInfo);
                    tvInfo.setText(series.getEpisodes().size() + "tập");

                    tvOverview.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (isExpanded) {
                                tvOverview.setMaxLines(3);
                                tvOverview.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                tvOverview.setMaxLines(Integer.MAX_VALUE);
                                tvOverview.setEllipsize(null);
                            }
                            isExpanded = !isExpanded;

                            // Cập nhật lại layout để RecyclerView di chuyển xuống
                            tvOverview.requestLayout();
                            tvOverview.invalidate();
                        }
                    });

                }
            }
            @Override
            public void onFailure(@NonNull Call<TVSeries> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });

    }

    @Override
    public void onEpisodeClick(String videoKey) {
        youTubePlayerInstance.loadVideo(videoKey, 0);
    }
}