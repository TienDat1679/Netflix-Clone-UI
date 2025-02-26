package com.netflixcloneui.screen.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, List<Movie>>> moviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(true); // Theo dõi trạng thái tải dữ liệu
    private final ApiService apiService;

    public HomeViewModel(Context context) {
        apiService = RetrofitClient.getApiService(context);
        fetchGenres();
    }

    public LiveData<List<Genre>> getGenres() {
        return genresLiveData;
    }

    public LiveData<Map<Long, List<Movie>>> getMovies() {
        return moviesLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    private void fetchGenres() {
        apiService.getGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body());
                    fetchMoviesByGenres(response.body());
                } else {
                    loadingLiveData.setValue(false); // Dừng loading nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy thể loại: " + t.getMessage());
                loadingLiveData.setValue(false);
            }
        });
    }

    private void fetchMoviesByGenres(List<Genre> genres) {
        Map<Long, List<Movie>> movieMap = new HashMap<>();
        final int totalGenres = genres.size();
        final int[] loadedGenres = {0}; // Đếm số thể loại đã tải xong phim

        for (Genre genre : genres) {
            apiService.getMoviesByGenre(genre.getId()).enqueue(new Callback<List<Movie>>() {
                @Override
                public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieMap.put(genre.getId(), response.body());
                    } else {
                        movieMap.put(genre.getId(), new ArrayList<>()); // Tránh null
                    }

                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        moviesLiveData.postValue(new HashMap<>(movieMap));
                        loadingLiveData.setValue(false); // Dừng loading khi tất cả phim đã tải xong
                    }
                }

                @Override
                public void onFailure(Call<List<Movie>> call, Throwable t) {
                    Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genre.getName() + ": " + t.getMessage());
                    movieMap.put(genre.getId(), new ArrayList<>());
                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        moviesLiveData.postValue(new HashMap<>(movieMap));
                        loadingLiveData.setValue(false);
                    }
                }
            });
        }
    }
}