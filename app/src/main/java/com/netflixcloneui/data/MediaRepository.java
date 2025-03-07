package com.netflixcloneui.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaRepository {
    private final ApiService apiService;

    public MediaRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void fetchMediaByGenres(List<Genre> genres, MutableLiveData<Map<Long, List<Media>>> media, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading
        Map<Long, List<Media>> mediaMap = new HashMap<>();
        final int totalGenres = genres.size();
        final int[] loadedGenres = {0};

        for (Genre genre : genres) {
            apiService.getMediaByGenre(genre.getId()).enqueue(new Callback<List<Media>>() {
                @Override
                public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mediaMap.put(genre.getId(), response.body());
                    } else {
                        mediaMap.put(genre.getId(), new ArrayList<>());
                    }

                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        media.setValue(new HashMap<>(mediaMap));
                        loadingLiveData.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<List<Media>> call, Throwable t) {
                    Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genre.getName() + ": " + t.getMessage());
                    mediaMap.put(genre.getId(), new ArrayList<>());
                    loadedGenres[0]++;
                    if (loadedGenres[0] == totalGenres) {
                        media.setValue(new HashMap<>(mediaMap));
                        loadingLiveData.setValue(false);
                    }
                }
            });
        }
    }

    public void fetchHotSeriesMovies(MutableLiveData<List<Media>> hotSeriesMovies, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getHotSeriesMovies().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hotSeriesMovies.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    hotSeriesMovies.setValue(new ArrayList<>());
                }
                loadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                hotSeriesMovies.setValue(null);
                loadingLiveData.setValue(false);
            }
        });
    }

    public void fetchComingSoon(MutableLiveData<List<Media>> comingSoonMovies, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getComingSoon().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comingSoonMovies.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    comingSoonMovies.setValue(new ArrayList<>());
                }
                loadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                comingSoonMovies.setValue(null);
                loadingLiveData.setValue(false);
            }
        });
    }

    public void fetchMediaByGenre(Long genreId, MutableLiveData<List<Media>> moviesLiveData, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getMediaByGenre(genreId).enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    moviesLiveData.setValue(response.body());
                } else {
                    moviesLiveData.setValue(new ArrayList<>()); // Trả về danh sách rỗng nếu lỗi
                }
                loadingLiveData.setValue(false); // Dừng loading
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim của thể loại " + genreId + ": " + t.getMessage());
                moviesLiveData.setValue(new ArrayList<>()); // Trả về danh sách rỗng nếu thất bại
                loadingLiveData.setValue(false); // Dừng loading
            }
        });
    }

    public void getTrendingMedia(Callback<List<Media>> callback) {
        apiService.getHotSeriesMovies().enqueue(callback);
    }

    public void searchMedia(String keyword, Callback<List<Media>> callback) {
        apiService.searchMedia(keyword).enqueue(callback);
    }
}
