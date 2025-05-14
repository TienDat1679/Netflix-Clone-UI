package com.netflixcloneui.ui;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.adapter.TrailerAdapter;
import com.netflixcloneui.adapter.ViewPaper2Adapter;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.databinding.ActivityMovieDetailBinding;
import com.netflixcloneui.databinding.ActivityTvSeriesDetailBinding;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Trailer;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.CreateCommentRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.model.request.PlaybackProgressRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.CommentResponse;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.viewmodel.UserViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView tvLike;

    private boolean isExpanded = false;
    private ImageView moviePoster, ivLike, ivAdd;
    private MaterialCardView btnClose;
    private MediaRepository mediaRepository;
    private TrailerAdapter trailerAdapter;
    private RecyclerView recyclerViewMovie;
    private Button btnPlay;
    private UserViewModel userViewModel;
    private List<Trailer> listTrailer;
    YouTubePlayerView youTubePlayerView;
    private List<Media> listMedia;
    private ActivityMovieDetailBinding binding;
    private ViewPaper2Adapter viewPaper2Adapter;
    private final String[] tabTitles = {"Nội dung tương tự", "Trailers", "Bình luận"};

    private boolean isPrenium=false;
    private boolean isPre=false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(getApplicationContext());
            }
        }).get(UserViewModel.class);
        mediaRepository = new MediaRepository(getApplicationContext());
        listTrailer = new ArrayList<>();
        moviePoster = findViewById(R.id.moviePoster);
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        btnPlay = findViewById(R.id.btnPlay);
        ivLike = findViewById(R.id.btnLike);
        tvLike = findViewById(R.id.tv_like);
        ivAdd = findViewById(R.id.iv_add);
        btnClose = findViewById(R.id.btnClose);
        long movieId = (long) getIntent().getLongExtra("media_id",-1);
        getTrailer(movieId);
        getMovieDetail(movieId);
        onMovieOverviewClick();
        close();
        handleLikeAndWatchlistButton(movieId);
        createComment(movieId);
        btnPlay.setOnClickListener(view -> playFullScreenVideo(movieId) );

        viewPaper2Adapter = new ViewPaper2Adapter(this);
        viewPaper2Adapter.addFragment(SimilarMediaFragment.newInstance(movieId));
        viewPaper2Adapter.addFragment(TrailerFragment.newInstance(movieId, listTrailer));
        if (getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("jwt_token", null) != null)
            viewPaper2Adapter.addFragment(CommentFragment.newInstance(movieId));
        binding.viewPager2.setAdapter(viewPaper2Adapter);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment currentFragment = viewPaper2Adapter.getFragment(position);
                LinearLayout commentBox = findViewById(R.id.commentBoxContainer);
                if (currentFragment instanceof CommentFragment) {
                    commentBox.setVisibility(View.VISIBLE);
                } else {
                    commentBox.setVisibility(View.GONE);
                }
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        checkPrenium();
    }

    private void checkPrenium() {

        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<ApiResponse<UserResponse>> call = apiService.getMyInfo();// Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse> >call, @NonNull Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> userResponseApiResponse = response.body();
                    String endDateStr = userResponseApiResponse.getResult().getEndDate();

                    if (endDateStr != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                            Date endDate = sdf.parse(endDateStr);
                            Date now = new Date();

                            if (now.before(endDate)) {
                                isPrenium = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace(); // handle parse error
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {

            }
        });
    }

    private void createComment(long mediaId) {
        userViewModel.getUserId().observe(this, userId -> {
            if (userId != null) {
                binding.btnSendComment.setOnClickListener(v -> {
                    String content = binding.etComment.getText().toString();
                    if (!content.isEmpty()) {
                        CreateCommentRequest request = new CreateCommentRequest(content, mediaId, userId);

                        RetrofitClient.getApiService(this).createComment(request).enqueue(new Callback<ApiResponse<CommentResponse>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<CommentResponse>> call, Response<ApiResponse<CommentResponse>> response) {
                                binding.etComment.setText("");
                                if (response.isSuccessful() && response.body() != null) {
                                    CommentFragment commentFragment = (CommentFragment) viewPaper2Adapter.getFragment(2);
                                    commentFragment.addNewComment(response.body().getResult());
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<CommentResponse>> call, Throwable t) {
                                Toast.makeText(MovieDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void showContinueWatchingDialog(Long savedPosition,Long mediaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiếp tục xem?");
        builder.setMessage("Bạn muốn tiếp tục xem từ phút " + (savedPosition / 60000) + " không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            Intent intent = new Intent(MovieDetailActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("postion",savedPosition);// Truyền videoId vào Intent
            startActivity(intent);
        });

        builder.setNegativeButton("Xem lại từ đầu", (dialog, which) -> {
            Intent intent = new Intent(MovieDetailActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("postion",0);// Truyền videoId vào Intent
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void playFullScreenVideo(Long movieId) {
        if (getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("jwt_token", null) != null) {
            if(isPre)
            {
                if(isPrenium)
                {
                    ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                    Call<PlaybackProgressRequest> call = apiService.getPlaybackProgress(movieId); // Không cần chuyển đổi bằng `Long.valueOf()`
                    call.enqueue(new Callback<PlaybackProgressRequest>() {
                        @Override
                        public void onResponse(@NonNull Call<PlaybackProgressRequest >call, @NonNull Response<PlaybackProgressRequest> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                PlaybackProgressRequest playbackProgressRequest = response.body();
                                showContinueWatchingDialog(playbackProgressRequest.getPosition(),movieId);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<PlaybackProgressRequest> call, @NonNull Throwable t) {
                            Intent intent = new Intent(MovieDetailActivity.this, FullScreenVideoActivity.class);
                            intent.putExtra("VIDEO_ID", movieId);
                            intent.putExtra("postion",0);// Truyền videoId vào Intent
                            startActivity(intent);
                        }
                    });
                }
                else {
                    showPreniumDialog();
                }
            }
            else {
                ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                Call<PlaybackProgressRequest> call = apiService.getPlaybackProgress(movieId); // Không cần chuyển đổi bằng `Long.valueOf()`
                call.enqueue(new Callback<PlaybackProgressRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<PlaybackProgressRequest >call, @NonNull Response<PlaybackProgressRequest> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            PlaybackProgressRequest playbackProgressRequest = response.body();
                            showContinueWatchingDialog(playbackProgressRequest.getPosition(),movieId);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<PlaybackProgressRequest> call, @NonNull Throwable t) {
                        Intent intent = new Intent(MovieDetailActivity.this, FullScreenVideoActivity.class);
                        intent.putExtra("VIDEO_ID", movieId);
                        intent.putExtra("postion",0);// Truyền videoId vào Intent
                        startActivity(intent);
                    }
                });
            }

        } else {
            TvSeriesDetailActivity.showLoginDialog(this);
        }
    }

    private void showPreniumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phim chỉ dành cho tài khoan prenium");
        builder.setMessage("Hãy trỏe thành thành vien prenium");

        builder.setPositiveButton("Đăng ki prenium", (dialog, which) -> {
            Intent intent = new Intent(MovieDetailActivity.this, PaymentPackageActivity.class);
            startActivity(intent);
        });

        builder.setNegativeButton("Đóng", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void close() {
        btnClose.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent); // báo cho fragment biết có thay đổi
            finish();
        });
    }

    private void getTrailer(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Trailer>> call = apiService.getmovieTrailer(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Trailer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Trailer> >call, @NonNull Response<List<Trailer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listTrailer = response.body();
                    if (!listTrailer.isEmpty()) {
                        binding.moviePoster.setVisibility(View.GONE);
                        binding.youtubePlayer.setVisibility(View.VISIBLE);
                        getLifecycle().addObserver(binding.youtubePlayer);
                        binding.youtubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
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

    private void handleLikeAndWatchlistButton(long movieId) {
        userViewModel.getUserId().observe(this, userId -> {
            if (userId != null) {
                userViewModel.checkMediaInLikeList(userId, movieId);
                userViewModel.getIsLike().observe(this, isLike -> {
                    if (isLike != null) {
                        ivLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isLike) {
                                    mediaRepository.likeMedia(userId, new LikeRequest(movieId, "movie"));
                                    userViewModel.setIsLike(true);
                                    ivLike.setImageResource(R.drawable.ic_liked);
                                    tvLike.setText("Đã thích !");
                                } else {
                                    mediaRepository.unlikeMedia(userId, movieId);
                                    userViewModel.setIsLike(false);
                                    ivLike.setImageResource(R.drawable.ic_like);
                                    tvLike.setText("Xếp hạng");
                                }
                            }
                        });
                    }
                });
                userViewModel.checkMediaInWatchList(userId, movieId, null);
                userViewModel.getIsInWatchList().observe(this, isInWatchList -> {
                    if (isInWatchList != null) {
                        ivAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isInWatchList) {
                                    mediaRepository.addToWatchList(userId, new AddToWatchListRequest(movieId, "movie"));
                                    userViewModel.setIsInWatchList(true);
                                    ivAdd.setImageResource(R.drawable.ic_added);
                                } else {
                                    mediaRepository.removeMediaFromWatchList(userId, movieId);
                                    userViewModel.setIsInWatchList(false);
                                    ivAdd.setImageResource(R.drawable.ic_add);
                                }
                            }
                        });
                    }
                });
            } else {
                binding.layoutActions.setVisibility(View.GONE);
            }
        });
        userViewModel.getIsLike().observe(this, isLike -> {
            if (isLike != null) {
                ivLike.setImageResource(isLike ? R.drawable.ic_liked : R.drawable.ic_like);
                tvLike.setText(isLike ? "Đã thích !" : "Xếp hạng");
            }
        });
        userViewModel.getIsInWatchList().observe(this, isLike -> {
            if (isLike != null) {
                ivAdd.setImageResource(isLike ? R.drawable.ic_added : R.drawable.ic_add);
            }
        });
    }

    private void getMovieDetail(long movieId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<Movie> call = apiService.getMovieDetail(movieId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<Movie>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();
                    if(movie.getIsPrenium()==1){
                        isPre=true;
                        binding.premium.setVisibility(View.VISIBLE);
                    }
                    binding.movieTitle.setText(movie.getTitle());
                    binding.movieOverview.setText(movie.getOverview());
                    binding.info.setText(movie.getReleaseDate() + "  |  " + movie.getRuntime() + " phút");
                    String posterUrl = "https://image.tmdb.org/t/p/w500/" + movie.getBackdropPath();
                    if (listTrailer.isEmpty()) {
                        binding.youtubePlayer.setVisibility(View.GONE);
                        binding.moviePoster.setVisibility(View.VISIBLE);
                        Glide.with(MovieDetailActivity.this)
                                .load(posterUrl)
                                .error(R.drawable.error_image)
                                .into( binding.moviePoster);
                    }
                }
                else {
                    Log.e("MovieDetail", "Response failed: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }

    private void onMovieOverviewClick() {
        binding.movieOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    binding.movieOverview.setMaxLines(3);
                    binding.movieOverview.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    binding.movieOverview.setMaxLines(Integer.MAX_VALUE);
                    binding.movieOverview.setEllipsize(null);
                }
                isExpanded = !isExpanded;

                // Cập nhật lại layout để RecyclerView di chuyển xuống
                binding.movieOverview.requestLayout();
                binding.movieOverview.invalidate();
            }
        });
    }
}