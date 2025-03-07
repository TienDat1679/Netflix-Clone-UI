package com.netflixcloneui.screen;
import com.netflixcloneui.R;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;

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
    private RecyclerView recyclerViewMovies;
    private MovieDetailAdapter movieAdapter;
    private List<Movie> movieList;

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

        long movieId = (long) getIntent().getLongExtra("media_id",-1);
        getMovieDetail(movieId);
        getListMovie(movieId);
    }

    private void getListMovie(long movieId){
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Movie> >call = apiService.getListMovieSame(movieId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie> >call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Movie > listMovie = response.body();

                    recyclerViewMovies = findViewById(R.id.recyclerViewMovies);

                    // Thiết lập RecyclerView
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewMovies.setLayoutManager(layoutManager);
                    movieAdapter = new MovieDetailAdapter(listMovie);
                    recyclerViewMovies.setAdapter(movieAdapter);

                    // Dùng SnapHelper để cuộn từng phim một cách mượt mà
                    SnapHelper snapHelper = new LinearSnapHelper();
                    snapHelper.attachToRecyclerView(recyclerViewMovies);

                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
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

                    moviePoster=  findViewById(R.id.moviePoster);

                    String posterUrl = imageUrl + movie.getBackdropPath();

                    movieRelease =findViewById(R.id.movieReleaseDate);
                    movieRelease.setText(movie.getReleaseDate());

                    movieRuntime = findViewById(R.id.movieRuntime);
                    movieRuntime.setText(movie.getRuntime() + " phút");

                    Log.d("movie poster", posterUrl);
                    Glide.with(MovieDetailActivity.this)
                            .load(posterUrl)
                            .error(R.drawable.error_image)
                            .into(moviePoster);

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