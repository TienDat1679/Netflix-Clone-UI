package com.netflixcloneui.screen;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.adapter.MovieDetailAdapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.Trailer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView movieTitle;
    private TextView movieOverview;
    boolean isExpanded = false;
    private ImageView moviePoster ;
    String imageUrl = "https://image.tmdb.org/t/p/w500";

    private TextView movieRelease;
    private TextView movieRuntime;
    private RecyclerView recyclerViewMovie;
    private MovieDetailAdapter movieAdapter;
    private List<Movie> movieList;
    List<Trailer> listTrailer;
    YouTubePlayerView youTubePlayerView;
    List <Media> listMedia;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        moviePoster=  findViewById(R.id.moviePoster);
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        long movieId = (long) getIntent().getLongExtra("media_id",-1);
        getTrailer(movieId);
        getMovieDetail(movieId);
        getMediaSame(movieId);
    }
    private void getMediaSame(long id)
    {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Media> >call = apiService.getSameMedia(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media> >call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listMedia= response.body();
                    recyclerViewMovie = findViewById(R.id.recyclerViewMovies);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MovieDetailActivity.this, 3); // 3 cột
                    recyclerViewMovie.setLayoutManager(gridLayoutManager);
                    recyclerViewMovie.setAdapter(new MediaAdapter(listMedia, MediaAdapter.TYPE_NORMAL));



                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Media>> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }
    private void getTrailer(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Trailer> >call = apiService.getmovieTrailer(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Trailer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Trailer> >call, @NonNull Response<List<Trailer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listTrailer = response.body();
                    if (!listTrailer.isEmpty() && listTrailer != null) {
                        moviePoster.setVisibility(View.GONE);
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        getLifecycle().addObserver(youTubePlayerView);
                        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(YouTubePlayer youTubePlayer) {
                                String videoKey = listTrailer.get(0).getKey(); // ID của video YouTube
                                youTubePlayer.loadVideo(videoKey, 0);
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Trailer>> call, @NonNull Throwable t) {
                Log.e("Trailer", "API Call failed: " + t.getMessage());
            }
        });
    }
    private  void getMovieDetail(long movieId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Movie> call = apiService.getMovieDetail(movieId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<Movie>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Movie movie = response.body();

                    movieTitle = findViewById(R.id.movieTitle);
                    movieTitle.setText(movie.getTitle());

                    movieOverview = findViewById(R.id.movieOverview);
                    movieOverview.setText(movie.getOverview());

                    String posterUrl = imageUrl + movie.getBackdropPath();

                    TextView info = (TextView) findViewById(R.id.info);

                    info.setText(movie.getReleaseDate() + "  |  " + movie.getRuntime() + " phut");

                    if(listTrailer.isEmpty()) {
                        youTubePlayerView.setVisibility(View.GONE);
                        moviePoster.setVisibility(View.VISIBLE);
                        Glide.with(MovieDetailActivity.this)
                                .load(posterUrl)
                                .error(R.drawable.error_image)
                                .into(moviePoster);
                    }
                    movieOverview.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (isExpanded) {
                                movieOverview.setMaxLines(3);
                                movieOverview.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                movieOverview.setMaxLines(Integer.MAX_VALUE);
                                movieOverview.setEllipsize(null);
                            }
                            isExpanded = !isExpanded;

                            // Cập nhật lại layout để RecyclerView di chuyển xuống
                            movieOverview.requestLayout();
                            movieOverview.invalidate();
                        }
                    });
                }

                else{
                    Log.e("MovieDetail", "Response failed: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }
}