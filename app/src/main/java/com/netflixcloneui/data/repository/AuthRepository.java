package com.netflixcloneui.data.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.request.LogoutRequest;
import com.netflixcloneui.ui.user.LoginActivity;
import com.netflixcloneui.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;
    public static AuthRepository instance;

    public AuthRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public static AuthRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AuthRepository(context);
        }
        return instance;
    }

    public void logout() {
        String token = tokenManager.getAccessToken();
        tokenManager.clearTokens();
        apiService.logout(new LogoutRequest(token)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    Log.d("Logout", "Đã đăng xuất.");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Api Error", t.getMessage());
            }
        });
    }


}
