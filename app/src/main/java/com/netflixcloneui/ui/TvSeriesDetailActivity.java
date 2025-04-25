package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.EpisodeAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.adapter.MovieDetailAdapter;
import com.netflixcloneui.adapter.ViewPaper2Adapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.databinding.ActivityTvSeriesDetailBinding;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.TVSeries;
import com.netflixcloneui.model.Trailer;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.utils.ViewPager2ViewHeightAnimator;
import com.netflixcloneui.viewmodel.UserViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvSeriesDetailActivity extends AppCompatActivity implements EpisodeAdapter.OnEpisodeClickListener{
    private TextView tvLike;
    private Button btnPlay;
    private MaterialCardView btnClose;
    private ImageView ivAdd, ivLike;
    boolean isExpanded = false;
    List<Trailer> listTrailer;
    private YouTubePlayer youTubePlayerInstance;
    private YouTubePlayerView youTubePlayerView;
    private MediaRepository mediaRepository;
    private UserViewModel userViewModel;
    private ActivityTvSeriesDetailBinding binding;
    private ViewPaper2Adapter viewPaper2Adapter;
    private final String[] tabTitles = {"Các tập", "Nội dung tương tự", "Trailers", "Bình luận"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTvSeriesDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(getApplicationContext());
            }
        }).get(UserViewModel.class);
        mediaRepository = new MediaRepository(getApplicationContext());
        ivAdd = findViewById(R.id.btnAdd);
        ivLike = findViewById(R.id.btnLike);
        btnPlay = findViewById(R.id.btnPlay);
        btnClose = findViewById(R.id.btnClose);
        tvLike = findViewById(R.id.tv_like);
        long id = (long) getIntent().getLongExtra("media_id",-1);
        getTvSeriesDetail(id);
        btnPlay.setOnClickListener(view -> playFullScreenVideo() );
        //getEsp(id);
        getTrailer(id);
        cLose();
        handleLikeAndWatchlistButton(id);

        viewPaper2Adapter = new ViewPaper2Adapter(this);
        viewPaper2Adapter.addFragment(EpisodeFragment.newInstance(id));
        viewPaper2Adapter.addFragment(SimilarMediaFragment.newInstance(id));
        viewPaper2Adapter.addFragment(TrailerFragment.newInstance(id, null));
        viewPaper2Adapter.addFragment(new CommentFragment());
        binding.viewPager2.setAdapter(viewPaper2Adapter);
        // Tự resize chiều cao mỗi khi thay tab
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                resizeViewPagerHeight(binding.viewPager2, position);
            }
        });
        // Resize lần đầu khi layout xong
        binding.viewPager2.post(() -> resizeViewPagerHeight(binding.viewPager2, binding.viewPager2.getCurrentItem()));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    private void resizeViewPagerHeight(ViewPager2 viewPager2, int position) {
        viewPager2.post(() -> {
            RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            if (viewHolder != null && viewHolder.itemView != null) {
                View itemView = viewHolder.itemView;

                itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(itemView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );

                int measuredHeight = itemView.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = viewPager2.getLayoutParams();
                layoutParams.height = measuredHeight;
                viewPager2.setLayoutParams(layoutParams);
            }
        });
    }

    private void playFullScreenVideo() {
        Intent intent = new Intent(this, FullScreenVideoActivity.class);
        intent.putExtra("VIDEO_ID", "xbsT5l4hdfA"); // Truyền videoId vào Intent
        startActivity(intent);
    }

    private void cLose() {
        btnClose.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent); // báo cho fragment biết có thay đổi
            finish();
        });
    }

    private void getTrailer(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Trailer>> call = apiService.getSeriesTrailer(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Trailer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Trailer> >call, @NonNull Response<List<Trailer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listTrailer = response.body();
                    if (!listTrailer.isEmpty()) {
                        binding.youtubePlayer.setVisibility(View.VISIBLE);
                        binding.moviePoster.setVisibility(View.GONE);
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

    private void handleLikeAndWatchlistButton(long id) {
        userViewModel.getUserId().observe(this, userId -> {
            if (userId != null) {
                userViewModel.checkMediaInLikeList(userId, id);
                userViewModel.getIsLike().observe(this, isLike -> {
                    if (isLike != null) {
                        ivLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isLike) {
                                    mediaRepository.likeMedia(userId, new LikeRequest(id, "tv_series"));
                                    userViewModel.setIsLike(true);
                                    ivLike.setImageResource(R.drawable.ic_liked);
                                    tvLike.setText("Đã thích !");
                                } else {
                                    mediaRepository.unlikeMedia(userId, id);
                                    userViewModel.setIsLike(false);
                                    ivLike.setImageResource(R.drawable.ic_like);
                                    tvLike.setText("Xếp hạng");
                                }
                            }
                        });
                    }
                });
                userViewModel.checkMediaInWatchList(userId, id, null);
                userViewModel.getIsInWatchList().observe(this, isInWatchList -> {
                    if (isInWatchList != null) {
                        ivAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isInWatchList) {
                                    mediaRepository.addToWatchList(userId, new AddToWatchListRequest(id, "tv_series"));
                                    userViewModel.setIsInWatchList(true);
                                    ivAdd.setImageResource(R.drawable.ic_added);
                                } else {
                                    mediaRepository.removeMediaFromWatchList(userId, id);
                                    userViewModel.setIsInWatchList(false);
                                    ivAdd.setImageResource(R.drawable.ic_add);
                                }
                            }
                        });
                    }
                });
            }
        });
        userViewModel.getIsInWatchList().observe(this, isLike -> {
            if (isLike != null) {
                ivAdd.setImageResource(isLike ? R.drawable.ic_added : R.drawable.ic_add);
            }
        });
        userViewModel.getIsLike().observe(this, isLike -> {
            if (isLike != null) {
                ivLike.setImageResource(isLike ? R.drawable.ic_liked : R.drawable.ic_like);
                tvLike.setText(isLike ? "Đã thích !" : "Xếp hạng");
            }
        });
    }

    private void getTvSeriesDetail(Long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<TVSeries> call = apiService.getTvSeriesDetail(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<TVSeries>() {
            @Override
            public void onResponse(@NonNull Call<TVSeries>call, @NonNull Response<TVSeries> response) {
                if (response.isSuccessful() && response.body() != null) {

                    TVSeries series= response.body();
                    TextView tvName= (TextView) findViewById(R.id.tvName);
                    tvName.setText(series.getName());

                    TextView tvOverview=(TextView) findViewById(R.id.tvOverview);
                    tvOverview.setText(series.getOverview());

                    TextView tvInfo= (TextView) findViewById(R.id.tvInfo);
                    tvInfo.setText(series.getFirstAirDate() + "  |  " + series.getEpisodes().size() + " tập");

                    if (listTrailer.isEmpty()) {
                        binding.youtubePlayer.setVisibility(View.GONE);
                        binding.moviePoster.setVisibility(View.VISIBLE);
                        Glide.with(TvSeriesDetailActivity.this)
                                .load("https://image.tmdb.org/t/p/w500/" + series.getBackdropPath())
                                .error(R.drawable.error_image)
                                .into( binding.moviePoster);
                    }

                    tvOverview.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (isExpanded) {
                                tvOverview.setMaxLines(3);
                                tvOverview.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                tvOverview.setMaxLines(Integer.MAX_VALUE);
                                tvOverview.setEllipsize(null);
                            }
                            isExpanded = !isExpanded;

                            // Cập nhật lại layout để RecyclerView di chuyển xuống
                            tvOverview.requestLayout();
                            tvOverview.invalidate();
                        }
                    });

                }
            }
            @Override
            public void onFailure(@NonNull Call<TVSeries> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });

    }

    @Override
    public void onEpisodeClick(String videoKey) {
        youTubePlayerInstance.loadVideo(videoKey, 0);
    }
}