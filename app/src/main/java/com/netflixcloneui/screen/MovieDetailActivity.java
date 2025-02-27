package com.netflixcloneui.screen;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netflixcloneui.R;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.MovieAdapter;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.LoginRequest;
import com.netflixcloneui.model.LoginResponse;
import com.netflixcloneui.model.MovieDetail;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    private RecyclerView recyclerViewMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        getMovieDetail();
        getListMovie();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getListMovie(){
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Movie> >call = apiService.getListMovieSame(18L); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie> >call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Movie > listMovie = response.body();

                    recyclerViewMovies = findViewById(R.id.recyclerViewMovies);


                    // Thiết lập RecyclerView
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewMovies.setLayoutManager(layoutManager);
                    movieAdapter = new MovieAdapter(listMovie);
                    recyclerViewMovies.setAdapter(movieAdapter);

                    // Dùng SnapHelper để cuộn từng phim một cách mượt mà
                    SnapHelper snapHelper = new LinearSnapHelper();
                    snapHelper.attachToRecyclerView(recyclerViewMovies);


                }
            }
            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }

    private  void getMovieDetail() {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<MovieDetail> call = apiService.getMovieDetail(100402L); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<MovieDetail>() {

            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {

                    MovieDetail movie = response.body();

                    movieTitle = findViewById(R.id.movieTitle);
                    movieTitle.setText(movie.getTitle());

                    movieOverview = findViewById(R.id.movieOverview);
                    movieOverview.setText(movie.getOverview());

                    moviePoster= (ImageView) findViewById(R.id.moviePoster);

                    String posterUrl = imageUrl + movie.getPosterPath();

                    Log.d("movie poster", posterUrl);
                    Glide.with(MovieDetailActivity.this)
                            .load(posterUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .placeholder(R.drawable.load_image)
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
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }
}