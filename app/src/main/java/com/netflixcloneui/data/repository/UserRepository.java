package com.netflixcloneui.data.repository;

import android.content.Context;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.utils.ApiErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService apiService;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void getMyInfo(RepositoryCallback<UserResponse> callback) {
        apiService.getMyInfo().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResult());
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    callback.onError(apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void checkMediaInWatchList(String userId, Long mediaId, RepositoryCallback<Boolean> callback) {
        apiService.checkMediaInWatchList(userId, mediaId).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResult());
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    callback.onError(apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void checkMediaInLikeList(String userId, Long mediaId, RepositoryCallback<Boolean> callback) {
        apiService.checkMediaInLikeList(userId, mediaId).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResult());
                } else {
                    ApiResponse<?> apiResponse = ApiErrorHandler.parseError(response);
                    callback.onError(apiResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
