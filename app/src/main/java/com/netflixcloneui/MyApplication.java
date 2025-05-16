package com.netflixcloneui;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.request.IntrospectRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.IntrospectResponse;
import com.netflixcloneui.ui.user.LoginActivity;
import com.netflixcloneui.utils.TokenManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication instance;
    private TokenManager tokenManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        tokenManager = TokenManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        checkTokenValidity(); // Kiểm tra token trong background thread
    }

    public static MyApplication getInstance() {
        return instance;
    }

    private void checkTokenValidity() {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            Log.d(TAG, "Không có token, bỏ qua kiểm tra");
            return;
        }

        executorService.execute(() -> {
            boolean isValid = isTokenValid(token);
            if (!isValid) {
                Log.d(TAG, "Token không hợp lệ, chuyển đến màn hình đăng nhập");
                mainHandler.post(this::navigateToLogin);
            } else {
                Log.d(TAG, "Token hợp lệ");
            }
        });
    }

    private boolean isTokenValid(String token) {
        try {
            ApiService apiService = RetrofitClient.getApiService(this);
            Response<ApiResponse<IntrospectResponse>> response = apiService.introspect(new IntrospectRequest(token)).execute();
            if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                return response.body().getResult().isValid();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra JWT", e);
        }
        return false;
    }

    private void navigateToLogin() {
        tokenManager.clearTokens();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}