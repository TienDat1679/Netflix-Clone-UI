package com.netflixcloneui.screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.EpisodeAdapter;
import com.netflixcloneui.adapter.MovieDetailAdapter;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvSeriesDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEps;

    boolean isExpanded = false;
    private EpisodeAdapter EpsAdapter;

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
        long id = (long) getIntent().getLongExtra("media_id",-1);
        getTvSeriesDetail(id);
        getEsp(id);
        ChangeRecycle(id);

    }
    private  void ChangeRecycle(long id){
        TextView sameMedia = findViewById(R.id.sameMedia);
        TextView esp = findViewById(R.id.esp);

        sameMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        esp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEsp(id);;
            }
        });
    }
    private void getEsp(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                List<Episode> listEps = response.body();

                recyclerViewEps = findViewById(R.id.recyclerEpisodes);

                // Thiết lập RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(TvSeriesDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerViewEps.setLayoutManager(layoutManager);
                EpsAdapter = new EpisodeAdapter(TvSeriesDetailActivity.this,listEps);
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
}