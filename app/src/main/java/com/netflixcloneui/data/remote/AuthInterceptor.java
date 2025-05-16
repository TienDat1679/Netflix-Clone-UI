package com.netflixcloneui.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.netflixcloneui.utils.JwtUtil;
import com.netflixcloneui.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = tokenManager.getAccessToken();

        if (token == null || shouldSkipAuth(originalRequest)) {
            Log.d("AuthInterceptor", "Token is null, request sent without Authorization header");
            return chain.proceed(originalRequest);
        }

        Request requestJWT = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(requestJWT);
    }

    private boolean shouldSkipAuth(Request request) {
        String path = request.url().encodedPath();
        return path.contains("/api/auth/token") ||
                path.contains("/api/auth/introspect") ||
                path.contains("/api/auth/refresh");
    }
}
