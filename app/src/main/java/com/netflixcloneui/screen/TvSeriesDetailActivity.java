package com.netflixcloneui.screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.netflixcloneui.R;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.TVSeries;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvSeriesDetailActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tv_series_detail);
        long id = (long) getIntent().getLongExtra("series_id",-1);
        getTvSeriesDetail(id);
        getEsp(id);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getEsp(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<TVSeries>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<TVSeries>>() {
            @Override
            public void onResponse(@NonNull Call<List<TVSeries>>call, @NonNull Response<List<TVSeries>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<TVSeries> series= response.body();




                }
            }
            @Override
            public void onFailure(@NonNull Call<List<TVSeries>> call, @NonNull Throwable t) {
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


                }
            }
            @Override
            public void onFailure(@NonNull Call<TVSeries> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });

    }
}