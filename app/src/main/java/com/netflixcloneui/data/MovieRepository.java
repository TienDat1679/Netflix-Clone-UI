package com.netflixcloneui.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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

public class MovieRepository {
    private final ApiService apiService;

    public MovieRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void fetchMoviesByGenres(List<Genre> genres, MutableLiveData<Map<Long, List<Movie>>> moviesLiveData, MutableLiveData<Boolean> loadingLiveData) {
        Map<Long, List<Movie>> movieMap = new HashMap<>();
        final int totalGenres = genres.size();
        final int[] loadedGenres = {0};

        for (Genre genre : genres) {
            apiService.getMoviesByGenre(genre.getId()).enqueue(new Callback<List<Movie>>() {
                @Override
                public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        movieMap.put(genre.getId(), response.body());
                    } else {
                        movieMap.put(genre.getId(), new ArrayList<>());
                    }

                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        moviesLiveData.postValue(new HashMap<>(movieMap));
                        loadingLiveData.setValue(false);
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

    public void fetchMovieByGenre(Long genreId, MutableLiveData<List<Movie>> moviesLiveData, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getMoviesByGenre(genreId).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    moviesLiveData.setValue(response.body());
                } else {
                    moviesLiveData.setValue(new ArrayList<>()); // Trả về danh sách rỗng nếu lỗi
                }
                loadingLiveData.setValue(false); // Dừng loading
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genreId + ": " + t.getMessage());
                moviesLiveData.setValue(new ArrayList<>()); // Trả về danh sách rỗng nếu thất bại
                loadingLiveData.setValue(false); // Dừng loading
            }
        });
    }

}
