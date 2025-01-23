package com.netflixcloneui.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8888/"; // URL của API

    private static Retrofit retrofit;

    // Phương thức để lấy Retrofit instance
    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Lấy SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

            // Tạo OkHttpClient với AuthInterceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sharedPreferences)) // Thêm AuthInterceptor
                    .build();

            // Cấu hình Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Gắn OkHttpClient đã cấu hình
                    .addConverterFactory(GsonConverterFactory.create()) // Gson converter
                    .build();
        }
        return retrofit;
    }

    // Phương thức để lấy ApiService
    public static ApiService getApiService(Context context) {
        return getRetrofitInstance(context).create(ApiService.class);
    }
}
