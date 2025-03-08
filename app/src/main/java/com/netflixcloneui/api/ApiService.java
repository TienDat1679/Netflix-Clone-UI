package com.netflixcloneui.api;

import com.netflixcloneui.model.ChangePasswordRequest;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.LoginRequest;
import com.netflixcloneui.model.LoginResponse;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.MovieDetail;
import com.netflixcloneui.model.RegisterRequest;
import com.netflixcloneui.model.TVSeries;

import java.util.List;

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
    Call<Movie> getMovieDetail(@Query("movieId") Long id);

    // Home Fragment
    @GET("api/genres")
    Call<List<Genre>> getGenres();
    @GET("api/genres/movies")
    Call<List<Genre>> getGenresForMovies();
    @GET("api/genres/series")
    Call<List<Genre>> getGenresForSeries();
    @GET("api/media/{genreId}")
    Call<List<Media>> getMediaByGenre(@Path("genreId") Long genreId);
    @GET("api/movies/{genreId}")
    Call<List<Media>> getMoviesByGenre(@Path("genreId") Long genreId);
    @GET("api/series/{genreId}")
    Call<List<Media>> getSeriesByGenre(@Path("genreId") Long genreId);

    @GET("/api/movies/search/movie-same")
    Call<List<Movie>> getListMovieSame(@Query("movieId") Long id);

    @GET("api/series/search")
    Call<TVSeries> getTvSeriesDetail(@Query("id") Long id);

    @GET("api/series/esp")
    Call<List<Episode>> getEspOfSeries(@Query("seriesId") Long id);
 // Coming Soon Fragment
    @GET("api/media/trending") // api test. Chưa có api thật
    Call<List<Media>> getComingSoon();
    @GET("api/media/trending")
    Call<List<Media>> getHotSeriesMovies();
    @GET("api/series/top10")
    Call<List<TVSeries>> getTopSeries();
    @GET("api/movies/top10")
    Call<List<Movie>> getTopMovies();
    @GET("api/media/series/top10")
    Call<List<Media>> getTop10Series();

    // My Netflix Fragment
    @GET("api/media/trending") // api test. Chưa có api thật
    Call<List<Media>> getUserFavoriteMovies();
    @GET("api/media/trending") // api test. Chưa có api thật
    Call<List<Media>> getUserMovieList();

    // Search
    @GET("api/media/search")
    Call<List<Media>> searchMedia(@Query("keyword") String keyword);

    // My Netflix Fragment

}
