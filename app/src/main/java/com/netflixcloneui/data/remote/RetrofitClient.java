package com.netflixcloneui.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://192.168.1.7:8888/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Tạo OkHttpClient với AuthInterceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // Thêm AuthInterceptor
                    .authenticator(new TokenAuthenticator(context))
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

    // Hữu ích khi muốn buộc tạo mới sau khi token thay đổi hoặc user logout
    public static void resetRetrofitInstance() {
        retrofit = null;
    }
}
