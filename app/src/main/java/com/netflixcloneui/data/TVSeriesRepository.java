package com.netflixcloneui.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.TVSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVSeriesRepository {
    private final ApiService apiService;

    public TVSeriesRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void fetchSeriesByGenres(List<Genre> genres, MutableLiveData<Map<Long, List<Media>>> series, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading
        Map<Long, List<Media>> seriesMap = new HashMap<>();
        final int totalGenres = genres.size();
        final int[] loadedGenres = {0};

        for (Genre genre : genres) {
            apiService.getSeriesByGenre(genre.getId()).enqueue(new Callback<List<Media>>() {
                @Override
                public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        seriesMap.put(genre.getId(), response.body());
                    } else {
                        seriesMap.put(genre.getId(), new ArrayList<>());
                    }

                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        series.postValue(new HashMap<>(seriesMap));
                        loadingLiveData.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<List<Media>> call, Throwable t) {
                    Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genre.getName() + ": " + t.getMessage());
                    seriesMap.put(genre.getId(), new ArrayList<>());
                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        series.postValue(new HashMap<>(seriesMap));
                        loadingLiveData.setValue(false);
                    }
                }
            });
        }
    }

    public void fetchTopTVSeries(MutableLiveData<List<TVSeries>> topSeries, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getTopSeries().enqueue(new Callback<List<TVSeries>>() {
            @Override
            public void onResponse(@NonNull Call<List<TVSeries>> call, @NonNull Response<List<TVSeries>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topSeries.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    topSeries.setValue(new ArrayList<>());
                }
                loadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<List<TVSeries>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                topSeries.setValue(null);
                loadingLiveData.setValue(false);
            }
        });
    }
}
