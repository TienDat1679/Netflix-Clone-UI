package com.netflixcloneui.screen;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netflixcloneui.R;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.model.LoginRequest;
import com.netflixcloneui.model.LoginResponse;
import com.netflixcloneui.model.MovieDetail;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView movieTitle;
    private TextView movieOverview;

    boolean isExpanded = false;

    private ImageView moviePoster ;
    String imageUrl = "https://image.tmdb.org/t/p/w500";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        getMovieDetail();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.e("GlideError", "Image Load Failed", e);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d("GlideSuccess", "Image Loaded Successfully");
                                    return false;
                                }
                            })
                            .centerCrop()
                            .placeholder(R.drawable.load_image)
                            .error(R.drawable.error_image)
                            .fallback(R.drawable.netflix_logo)
                            .into(moviePoster);


                    movieOverview.setOnClickListener(v -> {
                        if (isExpanded) {
                            movieOverview.setMaxLines(3);  // Thu gọn lại 3 dòng
                        } else {
                            movieOverview.setMaxLines(Integer.MAX_VALUE);  // Mở rộng toàn bộ
                        }
                        isExpanded = !isExpanded;
                    });



                    Log.d("MovieDetail", "Title: " + movie.getTitle());
                    Log.d("MovieDetail", "Overview: " + movie.getOverview());
                } else {
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