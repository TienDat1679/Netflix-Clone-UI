package com.netflixcloneui.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.utils.ApiErrorHandler;

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

    public void fetchTopTVSeries(MutableLiveData<List<Media>> topSeries, MutableLiveData<Boolean> loadingLiveData) {
        loadingLiveData.setValue(true); // Bắt đầu loading

        apiService.getTop10Series().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media>> call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topSeries.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Danh sách phim trống hoặc lỗi API");
                    topSeries.setValue(new ArrayList<>());
                }
                loadingLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi lấy phim sắp ra mắt: " + t.getMessage());
                topSeries.setValue(null);
                loadingLiveData.setValue(false);
            }
        });
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

    public void getTrendingMedia(Callback<List<Media>> callback) {
        apiService.getHotSeriesMovies().enqueue(callback);
    }

    public void searchMedia(String keyword, Callback<List<Media>> callback) {
        apiService.searchMedia(keyword).enqueue(callback);
    }

    public void likeMedia(String userId, LikeRequest request) {
        apiService.likeMedia(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("likeMedia", "Like successful");
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    Log.e("likeMedia", "Like failed: " + apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("likeMedia", "Like request failed", t);
            }
        });
    }

    public void unlikeMedia(String userId, Long mediaId) {
        apiService.removeMediaFromLikeList(userId, mediaId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("likeMedia", "Unlike successful");
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    Log.e("likeMedia", "Unlike failed: " + apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("likeMedia", "Like request failed", t);
            }
        });
    }

    public void removeMediaFromWatchList(String userId, Long mediaId) {
        apiService.removeMediaFromWatchList(userId, mediaId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("removeMediaFromWatchList", "Remove from watch list successful");
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    Log.e("removeMediaFromWatchList", "Remove from watch list failed: " + apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("removeMediaFromWatchList", "Remove from watch list request failed", t);
            }
        });
    }

    public void getLikeLists(String userId, RepositoryCallback<List<Media>> callback) {
        apiService.getLikeLists(userId).enqueue(new Callback<ApiResponse<List<Media>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Media>>> call, Response<ApiResponse<List<Media>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResult());
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    callback.onError(apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Media>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void addToWatchList(String userId, AddToWatchListRequest request) {
        apiService.addToWatchList(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("addToWatchList", "Add to watch list successful");
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    Log.e("addToWatchList", "Add to watch list failed: " + apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("addToWatchList", "Add to watch list request failed", t);
            }
        });
    }

    public void getWatchLists(String userId, RepositoryCallback<List<Media>> callback) {
        apiService.getWatchLists(userId).enqueue(new Callback<ApiResponse<List<Media>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Media>>> call, Response<ApiResponse<List<Media>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResult());
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    callback.onError(apiResponse.getMessage());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Media>>> call, Throwable t) {
                callback.onError(t.getMessage());;
            }
        });
    }
}
