package com.netflixcloneui.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Genre;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreRepository {
    private final ApiService apiService;

    public GenreRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void fetchGenres(MutableLiveData<List<Genre>> genresLiveData, MutableLiveData<Boolean> loadingLiveData) {
        apiService.getGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Không lấy được thể loại");
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy thể loại: " + t.getMessage());
                loadingLiveData.setValue(false);
            }
        });
    }

    public void fetchGenresForSeries(MutableLiveData<List<Genre>> genresLiveData, MutableLiveData<Boolean> loadingLiveData) {
        apiService.getGenresForSeries().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Không lấy được thể loại Series");
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy thể loại Series: " + t.getMessage());
                loadingLiveData.setValue(false);
            }
        });
    }

    public void fetchGenresForMovies(MutableLiveData<List<Genre>> genresLiveData, MutableLiveData<Boolean> loadingLiveData) {
        apiService.getGenresForMovies().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Không lấy được thể loại Phim");
                    loadingLiveData.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy thể loại Phim: " + t.getMessage());
                loadingLiveData.setValue(false);
            }
        });
    }

}
