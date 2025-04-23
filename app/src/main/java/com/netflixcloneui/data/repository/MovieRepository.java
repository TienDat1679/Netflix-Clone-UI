package com.netflixcloneui.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private final ApiService apiService;

    public MovieRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void fetchMoviesByGenres(List<Genre> genres, MutableLiveData<Map<Long, List<Media>>> movies, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading
        Map<Long, List<Media>> moviesMap = new HashMap<>();
        final int totalGenres = genres.size();
        final int[] loadedGenres = {0};

        for (Genre genre : genres) {
            apiService.getMoviesByGenre(genre.getId()).enqueue(new Callback<List<Media>>() {
                @Override
                public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        moviesMap.put(genre.getId(), response.body());
                    } else {
                        moviesMap.put(genre.getId(), new ArrayList<>());
                    }

                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        movies.setValue(new HashMap<>(moviesMap));
                        loadingLiveData.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<List<Media>> call, Throwable t) {
                    Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genre.getName() + ": " + t.getMessage());
                    moviesMap.put(genre.getId(), new ArrayList<>());
                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        movies.setValue(new HashMap<>(moviesMap));
                        loadingLiveData.setValue(false);
                    }
                }
            });
        }
    }

    public void fetchTopMovies(MutableLiveData<List<Media>> topMovies, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getTop10Movies().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topMovies.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    topMovies.setValue(new ArrayList<>());
                }
                loadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                topMovies.setValue(null);
                loadingLiveData.setValue(false);
            }
        });
    }
}
