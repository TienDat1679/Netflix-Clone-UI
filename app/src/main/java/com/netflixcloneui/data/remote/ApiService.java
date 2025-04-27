package com.netflixcloneui.data.remote;

import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.IntrospectRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.model.request.LogoutRequest;
import com.netflixcloneui.model.request.PlaybackProgressRequest;
import com.netflixcloneui.model.request.RefreshRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.request.ChangePasswordRequest;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.request.LoginRequest;
import com.netflixcloneui.model.response.AuthResponse;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.response.IntrospectResponse;
import com.netflixcloneui.model.response.QrResponse;
import com.netflixcloneui.model.request.RegisterRequest;
import com.netflixcloneui.model.TVSeries;
import com.netflixcloneui.model.Trailer;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.model.response.VNPayResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    // user's api
    @POST("api/auth/token")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);
    @POST("api/auth/logout")
    Call<Void> logout(@Body LogoutRequest request);
    @POST("api/auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body RefreshRequest request);
    @POST("api/auth/introspect")
    Call<ApiResponse<IntrospectResponse>> introspect(@Body IntrospectRequest request);
    @POST("api/register")
    Call<Void> registerAccount(@Body RegisterRequest request);
    @POST("api/register/verify/{otp}/{email}")
    Call<Void> verifyOtp(@Path("otp") String otp, @Path("email") String email);
    @GET("api/register/resend-otp/{email}")
    Call<Void> resendOtp(@Path("email") String email);
    @POST("api/forgotPassword/verifyMail/{email}")
    Call<Void> verifyForgotPasswordEmail(@Path("email") String email);
    @POST("api/forgotPassword/verifyOtp/{otp}/{email}")
    Call<Void> verifyForgotPasswordOtp(@Path("otp") String otp, @Path("email") String email);
    @POST("api/forgotPassword/changePassword/{email}")
    Call<Void> changePassword(@Path("email") String email, @Body ChangePasswordRequest request);
    @POST("api/forgotPassword/verifyMail/{email}")
    Call<Void> resendOtpFp(@Path("email") String email);
    @GET("users/my-info")
    Call<ApiResponse<UserResponse>> getMyInfo();

    // like, watch-list
    @POST("users/{userId}/likes")
    Call<Void> likeMedia(@Path("userId") String userId, @Body LikeRequest request);
    @GET("users/{userId}/likes")
    Call<ApiResponse<List<Media>>> getLikeLists(@Path("userId") String userId);
    @POST("users/{userId}/watch-lists")
    Call<Void> addToWatchList(@Path("userId") String userId, @Body AddToWatchListRequest request);
    @GET("users/{userId}/watch-lists")
    Call<ApiResponse<List<Media>>> getWatchLists(@Path("userId") String userId);
    @DELETE("users/{userId}/watch-lists/{mediaId}")
    Call<Void> removeMediaFromWatchList(@Path("userId") String userId, @Path("mediaId") Long mediaId);
    @DELETE("users/{userId}/likes/{mediaId}")
    Call<Void> removeMediaFromLikeList(@Path("userId") String userId, @Path("mediaId") Long mediaId);
    @GET("users/{userId}/watch-lists/{mediaId}")
    Call<ApiResponse<Boolean>> checkMediaInWatchList(@Path("userId") String userId, @Path("mediaId") Long mediaId);
    @GET("users/{userId}/likes/{mediaId}")
    Call<ApiResponse<Boolean>> checkMediaInLikeList(@Path("userId") String userId, @Path("mediaId") Long mediaId);

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
    @GET("api/media/movie/top10")
    Call<List<Media>> getTop10Movies();

    // My Netflix Fragment
    @GET("api/media/trending") // api test. Chưa có api thật
    Call<List<Media>> getUserFavoriteMovies();
    @GET("api/media/trending") // api test. Chưa có api thật
    Call<List<Media>> getUserMovieList();

    // Search
    @GET("api/media/search")
    Call<List<Media>> searchMedia(@Query("keyword") String keyword);
    // My Netflix Fragment
    @GET("api/media/same")
    Call<List<Media>> getSameMedia(@Query("id") Long id);
    @GET("api/series/trailer")
    Call<List<Trailer>> getSeriesTrailer(@Query("id") Long id);
    @GET("api/movies/trailer")
    Call<List<Trailer>> getmovieTrailer(@Query("id") Long id);
    @GET("api/trailers/{mediaId}")
    Call<ApiResponse<List<Trailer>>> getMediaTrailers(@Path("mediaId") Long mediaId);

    @GET("api/vnpay/generateQR")
    Call<QrResponse> generateVnpayQR(@Query("amount") String  amount);

    @GET("api/vnpay/callback")
    Call<VNPayResponse> checkPayment(@QueryMap Map<String, String> params);

    @GET("api/playback")
    Call<PlaybackProgressRequest> getPlaybackProgress( @Query("mediaId") Long mediaId);
    @POST("/api/playback/save")
    Call<Void> savePlaybackProgress(@Query("mediaId") Long mediaId,
                                    @Query("position") Long position);


}
