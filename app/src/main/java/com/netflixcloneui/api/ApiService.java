package com.netflixcloneui.api;

import com.netflixcloneui.model.ChangePasswordRequest;
import com.netflixcloneui.model.LoginRequest;
import com.netflixcloneui.model.LoginResponse;
import com.netflixcloneui.model.MovieDetail;
import com.netflixcloneui.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("api/register")
    Call<Void> registerAccount(@Body RegisterRequest request);
    @GET("api/register/verify/{otp}/{email}")
    Call<Void> verifyOtp(
        @Path("otp") String otp,
        @Path("email") String email
    );
    @GET("api/register/resend-otp/{email}")
    Call<Void> resendOtp(@Path("email") String email);
    @POST("api/forgotPassword/verifyMail/{email}")
    Call<Void> verifyForgotPasswordEmail(@Path("email") String email);
    @POST("api/forgotPassword/verifyOtp/{otp}/{email}")
    Call<Void> verifyForgotPasswordOtp(@Path("otp") String otp, @Path("email") String email);
    @POST("api/forgotPassword/changePassword/{email}")
    Call<Void> changePassword(
            @Path("email") String email,
            @Body ChangePasswordRequest request
            );
    @POST("api/forgotPassword/resend-otp/{email}")
    Call<Void> resendOtpFp(@Path("email") String email);


    @GET("api/movies/search")
    Call<MovieDetail> getMovieDetail(@Query("id") Long id);
}
