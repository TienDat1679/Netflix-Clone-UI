package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.netflixcloneui.model.request.CreateCommentRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.model.request.PlaybackProgressRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.CommentResponse;
import com.netflixcloneui.utils.ViewPager2ViewHeightAnimator;
import com.netflixcloneui.viewmodel.UserViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    private List<Episode> listEps;

    private  Long episodeIdOne;
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
        listTrailer = new ArrayList<>();
        long id = (long) getIntent().getLongExtra("media_id",-1);
        getEsp(id);
        getTvSeriesDetail(id);
        getSeason(id);
        btnPlay.setOnClickListener(view -> playFullScreenVideo(id) );
        //getEsp(id);
        getTrailer(id);
        cLose();
        handleLikeAndWatchlistButton(id);
        createComment(id);

        viewPaper2Adapter = new ViewPaper2Adapter(this);
        viewPaper2Adapter.addFragment(EpisodeFragment.newInstance(id,1));
        viewPaper2Adapter.addFragment(SimilarMediaFragment.newInstance(id));
        viewPaper2Adapter.addFragment(TrailerFragment.newInstance(id, null));
        viewPaper2Adapter.addFragment(CommentFragment.newInstance(id));
        binding.viewPager2.setAdapter(viewPaper2Adapter);
        // Tự resize chiều cao mỗi khi thay tab
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                resizeViewPagerHeight(binding.viewPager2, position);

                Fragment currentFragment = viewPaper2Adapter.getFragment(position);

                LinearLayout commentBox = findViewById(R.id.commentBoxContainer);

                if (currentFragment instanceof CommentFragment) {
                    commentBox.setVisibility(View.VISIBLE);
                } else {
                    commentBox.setVisibility(View.GONE);
                }
            }
        });
        // Resize lần đầu khi layout xong
        binding.viewPager2.post(() -> resizeViewPagerHeight(binding.viewPager2, binding.viewPager2.getCurrentItem()));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
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
                                    CommentFragment commentFragment = (CommentFragment) viewPaper2Adapter.getFragment(3);
                                    commentFragment.addNewComment(response.body().getResult());
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<CommentResponse>> call, Throwable t) {
                                Toast.makeText(TvSeriesDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void getSeason(long id) {
        // 1. Gọi API để lấy danh sách tất cả episode của mediaId
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id);
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>> call,
                                   @NonNull Response<List<Episode>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("getSeason", "API returned empty or error");
                    return;
                }

                // 2. Lưu toàn bộ listEps và tách ra các season duy nhất
                listEps = response.body();
                Set<Integer> seasonsSet = new TreeSet<>();
                for (Episode ep : listEps) {
                    seasonsSet.add(ep.getSeasonNumber());
                }

                // 3. Chuyển Set -> List<String> để gán cho Spinner
                List<String> seasonList = new ArrayList<>();
                for (Integer s : seasonsSet) {
                    seasonList.add("Season " + s);
                }

                // 4. Thiết lập Spinner
                Spinner spinnerSeasons = findViewById(R.id.spinnerSeasons);
                ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(
                        TvSeriesDetailActivity.this,                                      // context
                        R.layout.spinner_item_white,                            // layout cho item
                        seasonList
                );
                spinnerSeasons.setAdapter(seasonAdapter);

                // 5. Chọn mặc định "Season 1" nếu có
                int defaultIndex = seasonList.indexOf("Season 1");
                spinnerSeasons.setSelection(defaultIndex >= 0 ? defaultIndex : 0);

                // 6. Lắng nghe sự kiện chọn season mới
                spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        String label = (String) parent.getItemAtPosition(position);
                        int newSeason = Integer.parseInt(label.split(" ")[1]);
                        Log.d("getSeason", "User selected season " + newSeason);

                        // 7. Lấy EpisodeFragment trong ViewPager2 và gọi reload
                        EpisodeFragment epFrag = (EpisodeFragment)
                                viewPaper2Adapter.getFragment(0);    // tab 0 là EpisodeFragment
                        if (epFrag != null) {
                            epFrag.reload(newSeason);
                        }
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("getSeason", "API call failed: " + t.getMessage());
            }
        });
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
    private void showContinueWatchingDialog(Long savedPosition,Long mediaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiếp tục xem?");
        builder.setMessage("Bạn muốn tiếp tục xem từ phút " + (savedPosition / 60000) + " không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            Intent intent = new Intent(TvSeriesDetailActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("postion",savedPosition);// Truyền videoId vào Intent
            startActivity(intent);
        });

        builder.setNegativeButton("Xem lại từ đầu", (dialog, which) -> {
            Intent intent = new Intent(TvSeriesDetailActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("postion",0);// Truyền videoId vào Intent
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void playFullScreenVideo(Long episodeId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<PlaybackProgressRequest> call = apiService.getPlaybackProgress(episodeId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<PlaybackProgressRequest>() {
            @Override
            public void onResponse(@NonNull Call<PlaybackProgressRequest >call, @NonNull Response<PlaybackProgressRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaybackProgressRequest playbackProgressRequest = response.body();
                    showContinueWatchingDialog(playbackProgressRequest.getPosition(),episodeId);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PlaybackProgressRequest> call, @NonNull Throwable t) {
                Intent intent = new Intent(TvSeriesDetailActivity.this, FullScreenVideoActivity.class);
                intent.putExtra("VIDEO_ID", episodeId);
                intent.putExtra("postion",0);// Truyền videoId vào Intent
                startActivity(intent);
            }
        });
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
    private void getEsp(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                listEps = response.body();
                episodeIdOne = listEps.get(0).getId();
            }
            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("Tvseries Eps", "API Call failed: " + t.getMessage());
            }
        });
    }
    @Override
    public void onEpisodeClick(Long episodeId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<PlaybackProgressRequest> call = apiService.getPlaybackProgress(episodeId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<PlaybackProgressRequest>() {
            @Override
            public void onResponse(@NonNull Call<PlaybackProgressRequest >call, @NonNull Response<PlaybackProgressRequest> response) {
                if (response.isSuccessful() && response.body() != null) {

                    PlaybackProgressRequest playbackProgressRequest = response.body();
                    showContinueWatchingDialog(playbackProgressRequest.getPosition(),episodeId);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PlaybackProgressRequest> call, @NonNull Throwable t) {
                Intent intent = new Intent(TvSeriesDetailActivity.this, FullScreenVideoActivity.class);
                intent.putExtra("VIDEO_ID", episodeId);
                intent.putExtra("postion",0);// Truyền videoId vào Intent
                startActivity(intent);
            }
        });
    }
}