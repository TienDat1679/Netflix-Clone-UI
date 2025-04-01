package com.netflixcloneui.ui.mynetflix;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Media;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNetflixViewModel extends ViewModel {

    private final MutableLiveData<List<Media>> favoriteMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> userMovieList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final ApiService apiService;

    public MyNetflixViewModel(Context context) {
        apiService = RetrofitClient.getApiService(context);
        fetchUserFavoriteMovies();
        fetchUserMovieList();
    }

    public LiveData<List<Media>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public LiveData<List<Media>> getUserMovieList() {
        return userMovieList;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    private void fetchUserFavoriteMovies() {
        apiService.getUserFavoriteMovies().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteMovies.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim yêu thích trống hoặc lỗi API");
                    favoriteMovies.setValue(null);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Media>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy danh sách phim yêu thích: " + t.getMessage());
                favoriteMovies.setValue(null);
                isLoading.setValue(false);
            }
        });
    }

    private void fetchUserMovieList() {
        apiService.getUserMovieList().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userMovieList.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách của tôi trống hoặc lỗi API");
                    userMovieList.setValue(null);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Media>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy danh sách của tôi: " + t.getMessage());
                userMovieList.setValue(null);
                isLoading.setValue(false);
            }
        });
    }
}