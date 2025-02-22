package com.netflixcloneui.api;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SharedPreferences sharedPreferences;

    public AuthInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        // Lấy JWT token từ SharedPreferences
        String token = sharedPreferences.getString("jwt_token", null);

        Request request = chain.request();

        // Thêm token vào tiêu đề nếu token không null
        if (token != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        } else {
            Log.d("AuthInterceptor", "Token is null, request sent without Authorization header");
        }

        return chain.proceed(request);
    }
}
