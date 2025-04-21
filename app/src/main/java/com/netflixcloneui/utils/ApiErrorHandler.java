package com.netflixcloneui.utils;

import com.google.gson.Gson;
import com.netflixcloneui.model.response.ApiResponse;

import retrofit2.Response;

public class ApiErrorHandler {
    public static ApiResponse<?> parseError(Response<?> response) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(response.errorBody().string(), ApiResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(500, null, "Lỗi không xác định");
        }
    }
}
