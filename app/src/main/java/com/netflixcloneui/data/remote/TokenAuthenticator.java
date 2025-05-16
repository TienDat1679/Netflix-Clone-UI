package com.netflixcloneui.data.remote;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netflixcloneui.model.request.RefreshRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.AuthResponse;
import com.netflixcloneui.utils.TokenManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Authenticator {
    private static final String TAG = "TokenAuthenticator";
    private final Context context;
    private final TokenManager tokenManager;
    private static final Object refreshTokenLock = new Object(); // Biến lock để đồng bộ refresh token
    private static String lastTokenRefreshed = null;

    public TokenAuthenticator(Context context) {
        this.context = context;
        this.tokenManager = TokenManager.getInstance(context);
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) {
        Log.d(TAG, "Authentication challenge received, attempting to refresh token");

        // Check if response code is 401 (Unauthorized) & Don't retry if we've already failed to authenticate
        if (response.code() != 401 || responseCount(response) >= 3) {
            return null;
        }

        String currentToken = tokenManager.getAccessToken(); // Get current refresh token
        Log.d(TAG, "Current refresh token: " + currentToken);
        if (currentToken == null || currentToken.isEmpty()) {
            Log.d(TAG, "No refresh token available");
            tokenManager.clearTokens();
            return null;
        }

        // === Đồng bộ refresh token ===
        String newAccessToken;
        synchronized (refreshTokenLock) {
            String latest = tokenManager.getAccessToken();

            // Nếu token đã được thread khác refresh thành công rồi, dùng luôn
            if (!latest.equals(currentToken)) {
                Log.d(TAG, "Token already refreshed by another thread");
                newAccessToken = latest;
            } else {
                // Gọi refresh token
                newAccessToken = refreshTokenSync(currentToken);
                lastTokenRefreshed = newAccessToken;
            }
        }

        if (newAccessToken == null) {
            Log.d(TAG, "Token refresh failed");
            return null;
        }

        // Add new token to request and retry
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }

    private String refreshTokenSync(String refreshToken) {
        Log.d(TAG, "Attempting synchronous token refresh");

        // Create a separate retrofit instance without the authenticator to avoid infinite loop
        Retrofit retrofitWithoutAuthenticator = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofitWithoutAuthenticator.create(ApiService.class);
        RefreshRequest refreshRequest = new RefreshRequest(refreshToken);

        try {
            // Synchronous call to refresh token
            retrofit2.Response<ApiResponse<AuthResponse>> response = apiService
                    .refreshToken(refreshRequest)
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                AuthResponse authResponse = response.body().getResult();
                if (authResponse != null) {
                    tokenManager.saveTokens(authResponse.getToken());
                    Log.d(TAG, "Token refresh successful");
                    Log.d(TAG, "New access token: " + authResponse.getToken());
                    return authResponse.getToken();
                }
            } else {
                Log.d(TAG, "Token refresh failed: " + (response.errorBody() != null ?
                        response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception during token refresh", e);
        }

        return null;
    }

    // Count retries to prevent infinite retry loops
    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }
}