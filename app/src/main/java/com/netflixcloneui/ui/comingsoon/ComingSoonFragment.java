package com.netflixcloneui.ui.comingsoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.ComingSoonAdapter;
import com.netflixcloneui.adapter.HotAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.data.repository.RepositoryCallback;
import com.netflixcloneui.databinding.FragmentComingSoonBinding;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.PlayBackResponse;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.ui.FullScreenVideoActivity;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.ui.PaymentPackageActivity;
import com.netflixcloneui.ui.SearchActivity;
import com.netflixcloneui.ui.TvSeriesDetailActivity;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComingSoonFragment extends Fragment {
    private FragmentComingSoonBinding binding;
    private ComingSoonViewModel comingSoonViewModel;
    private ComingSoonAdapter comingSoonAdapter;
    private HotAdapter hotAdapter, topMoviesAdapter, topSeriesAdapter;
    private UserViewModel userViewModel;
    private MediaRepository mediaRepository;
    private boolean isPre = false;
    private boolean isPrenium = false;
    private Episode firstEpisode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        comingSoonViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ComingSoonViewModel(requireContext());
            }
        }).get(ComingSoonViewModel.class);
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(requireContext());
            }
        }).get(UserViewModel.class);

        mediaRepository = new MediaRepository(requireContext());
        userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
            if (userId != null) {
                userViewModel.fetchUserWatchList(userId);

                userViewModel.getWatchList().observe(getViewLifecycleOwner(), watchList -> {
                   if (watchList != null) {
                       loadHotMedia(userId);
                       loadTopSeries(userId);
                       loadTopMovies(userId);
                   }
                });
            } else {
                loadHotMedia(null);
                loadTopSeries(null);
                loadTopMovies(null);
            }
        });

        binding = FragmentComingSoonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnComingSoon.setOnClickListener(v -> scrollToRecyclerView(binding.rcvComingSoon));
        binding.btnHot.setOnClickListener(v -> scrollToRecyclerView(binding.rcvHot));
        binding.btnTopSeries.setOnClickListener(v -> scrollToRecyclerView(binding.rcvTopSeries));
        binding.btnTopMovies.setOnClickListener(v -> scrollToRecyclerView(binding.rcvTopMovies));

        loadComingSoon();
        loading();

        return root;
    }

    private void loadComingSoon() {
        comingSoonAdapter = new ComingSoonAdapter(new ComingSoonAdapter.OnMediaClickListener() {
            @Override
            public void onRemindClick(Media media, int position, ComingSoonAdapter.ComingSoonViewHolder holder) {
                ApiService apiService = RetrofitClient.getApiService(getContext());

                if (!holder.buttonNotification.getText().equals("Đã đặt lời nhắc")) {
                    Call<Void> call = apiService.createReminder(media.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                media.setRemind(true);
                                comingSoonAdapter.notifyItemChanged(position);
                                holder.buttonNotification.setText("Đã đặt lời nhắc");
                                holder.buttonNotification.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_added));
                                holder.buttonNotification.setIconTintResource(R.color.black);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                } else {
                    Call<Void> call = apiService.deleteReminder(media.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                media.setRemind(false);
                                comingSoonAdapter.notifyItemChanged(position);
                                holder.buttonNotification.setText("Nhắc tôi");
                                holder.buttonNotification.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_notifications_black_24dp));
                                holder.buttonNotification.setIconTintResource(R.color.black);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            }
        });
        binding.rcvComingSoon.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvComingSoon.setAdapter(comingSoonAdapter);

        comingSoonViewModel.getComingSoonMedia().observe(getViewLifecycleOwner(), media -> {
            if (media != null) {
                comingSoonAdapter.setMedia(media);
            }
        });
    }

    private void loadHotMedia(String userId) {
        hotAdapter = initAdapter(userId, HotAdapter.TYPE_HOT);
        binding.rcvHot.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvHot.setAdapter(hotAdapter);

        comingSoonViewModel.getHotMedia().observe(getViewLifecycleOwner(), media -> {
            if (media != null) {
                hotAdapter.setMedia(media);
            }
        });
    }

    private void loadTopSeries(String userId) {
        topSeriesAdapter = initAdapter(userId, HotAdapter.TYPE_TOP_10);
        binding.rcvTopSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvTopSeries.setAdapter(topSeriesAdapter);

        comingSoonViewModel.getTopSeries().observe(getViewLifecycleOwner(), series -> {
            if (series != null) {
                topSeriesAdapter.setMedia(series);
            }
        });
    }

    private void loadTopMovies(String userId) {
        topMoviesAdapter = initAdapter(userId, HotAdapter.TYPE_TOP_10);
        binding.rcvTopMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvTopMovies.setAdapter(topMoviesAdapter);

        comingSoonViewModel.getTopMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                topMoviesAdapter.setMedia(movies);
            }
        });
    }

    private void scrollToRecyclerView(View recyclerView) {
        binding.nestedScrollView.post(() -> binding.nestedScrollView.smoothScrollTo(0, recyclerView.getTop()));
    }

    private void loading() {
        comingSoonViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
    }

    private HotAdapter initAdapter(String userId, int viewType) {
        if (userId == null) {
            return new HotAdapter(viewType, userViewModel, new HotAdapter.OnMediaClickListener() {
                @Override
                public void onPlayClick(Media media) {

                }

                @Override
                public void onAddClick(Media media, HotAdapter.HotViewHolder holder) {

                }

                @Override
                public void onMediaDetailClick(Media media) {
                    openMediaDetail(requireContext(), media.getId(), media.getType());
                }
            });
        }
        return new HotAdapter(viewType, userViewModel, new HotAdapter.OnMediaClickListener() {
            @Override
            public void onPlayClick(Media media) {
                if (media.getIsPrenium() == 1) {
                    isPre = true;
                } else {
                    isPre = false;
                }

                Long mediaId = media.getId();
                if (Objects.equals(media.getType(), "tv_series")) {
                    getEpisode(mediaId);
                } else {
                    playFullScreenVideo(mediaId);
                }
            }

            @Override
            public void onAddClick(Media media, HotAdapter.HotViewHolder holder) {
                userViewModel.checkMediaInWatchList(userId, media.getId(), new RepositoryCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isInWatchList) {
                        if (Boolean.TRUE.equals(isInWatchList)) {
                            mediaRepository.removeMediaFromWatchList(userId, media.getId());
                            userViewModel.setIsInWatchList(false);
                            holder.buttonAdd.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_add));
                        } else {
                            mediaRepository.addToWatchList(userId, new AddToWatchListRequest(media.getId(), media.getType()));
                            userViewModel.setIsInWatchList(true);
                            holder.buttonAdd.setIcon(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_added));
                        }

                        // Cập nhật lại danh sách
                        //userViewModel.fetchUserWatchList(userId);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("onAddClick", message);
                    }
                });
            }

            @Override
            public void onMediaDetailClick(Media media) {
                openMediaDetail(requireContext(), media.getId(), media.getType());
            }
        });
    }

    private void openMediaDetail(Context context, Long id, String type) {
        if ("movie".equals(type)) {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("media_id", id); // Truyền ID phim
            launcher.launch(intent);
        } else {
            Intent intent = new Intent(context, TvSeriesDetailActivity.class);
            intent.putExtra("media_id", id); // Truyền ID phim
            launcher.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
                        if (userId != null) {
                            userViewModel.fetchUserWatchList(userId);
                        }
                    });
                }
            });

    private void getEpisode(long id) {
        ApiService apiService = RetrofitClient.getApiService(getActivity());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    firstEpisode = response.body().get(0);
                    playFullScreenVideo(firstEpisode.getId());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("Tvseries Eps", "API Call failed: " + t.getMessage());
            }
        });
    }

    public void playFullScreenVideo(Long episodeIdOne) {
        Log.d("esp",String.valueOf(episodeIdOne));
        if (getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).getString("jwt_token", null) != null) {
            if(isPre)
            {
                if(isPrenium)
                {
                    ApiService apiService = RetrofitClient.getApiService(getContext());
                    Call<PlayBackResponse> call = apiService.getPlaybackProgress(episodeIdOne); // Không cần chuyển đổi bằng `Long.valueOf()`
                    call.enqueue(new Callback<PlayBackResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                PlayBackResponse PlayBackResponse = response.body();
                                showContinueWatchingDialog(PlayBackResponse.getPosition(),episodeIdOne);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                            intent.putExtra("VIDEO_ID", episodeIdOne);
                            intent.putExtra("position",0);// Truyền videoId vào Intent
                            startActivity(intent);
                        }
                    });
                }
                else {
                    showPreniumDialog();
                }
            }
            else {
                ApiService apiService = RetrofitClient.getApiService(getContext());
                Call<PlayBackResponse> call = apiService.getPlaybackProgress(episodeIdOne); // Không cần chuyển đổi bằng `Long.valueOf()`
                call.enqueue(new Callback<PlayBackResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            PlayBackResponse PlayBackResponse = response.body();
                            showContinueWatchingDialog(PlayBackResponse.getPosition(),episodeIdOne);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                        Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                        intent.putExtra("VIDEO_ID", episodeIdOne);
                        intent.putExtra("postion",0);// Truyền videoId vào Intent
                        startActivity(intent);
                    }
                });
            }

        } else {
            TvSeriesDetailActivity.showLoginDialog(getContext());
        }
    }

    private void showContinueWatchingDialog(Long savedPosition,Long mediaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tiếp tục xem?");
        builder.setMessage("Bạn muốn tiếp tục xem từ phút " + (savedPosition / 60000) + " không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",savedPosition);// Truyền videoId vào Intent
            startActivity(intent);
        });

        builder.setNegativeButton("Xem lại từ đầu", (dialog, which) -> {
            ApiService apiService = RetrofitClient.getApiService(getContext());
            Call<Void> call = apiService.deletePlayback(mediaId);// Không cần chuyển đổi bằng `Long.valueOf()`
            call.enqueue(new Callback<Void>() {
                             @Override
                             public void onResponse(Call<Void> call, Response<Void> response) {

                             }

                             @Override
                             public void onFailure(Call<Void> call, Throwable t) {

                             }
                         }

            );
            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",0);// Truyền videoId vào Intent
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPreniumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Phim chỉ dành cho tài khoan prenium");
        builder.setMessage("Hãy trỏe thành thành vien prenium");

        builder.setPositiveButton("Đăng ki prenium", (dialog, which) -> {
            Intent intent = new Intent(getContext(), PaymentPackageActivity.class);
            startActivity(intent);
        });

        builder.setNegativeButton("Đóng", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkPrenium() {
        ApiService apiService = RetrofitClient.getApiService(getContext());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}