package com.netflixcloneui.screen.ui.coming_soon;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Movie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComingSoonViewModel extends ViewModel {

    private final MutableLiveData<List<Movie>> comingSoonMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final ApiService apiService;

    public ComingSoonViewModel(Context context) {
        apiService = RetrofitClient.getApiService(context);
        fetchComingSoonMovies();
    }

    public LiveData<List<Movie>> getComingSoonMovies() {
        return comingSoonMovies;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    private void fetchComingSoonMovies() {
        apiService.getComingSoonMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();

                    // In danh sách phim nhận được từ API
                    Log.d("API_SUCCESS", "Số lượng phim sắp ra mắt: " + movies.size());
                    for (Movie movie : movies) {
                        Log.d("MOVIE", "ID: " + movie.getId() + " | Title: " + movie.getTitle());
                    }

                    comingSoonMovies.setValue(movies);
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    comingSoonMovies.setValue(new ArrayList<>());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                comingSoonMovies.setValue(null);
                isLoading.setValue(false);
            }
        });
    }
}