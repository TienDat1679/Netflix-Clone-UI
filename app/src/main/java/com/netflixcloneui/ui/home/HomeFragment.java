package com.netflixcloneui.ui.home;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.PlayBackResponse;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.ui.FullScreenVideoActivity;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.GenreAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.databinding.FragmentHomeBinding;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.ui.PaymentPackageActivity;
import com.netflixcloneui.ui.SearchActivity;
import com.netflixcloneui.ui.TvSeriesDetailActivity;
import com.netflixcloneui.ui.user.LoginActivity;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GenreAdapter genreAdapter;
    private MediaAdapter moviesByGenreAdapter, top10SeriesAdapter, top10MoviesAdapter;
    //private MovieAdapter.OnMovieClickListener movieClickListener;
    private HomeViewModel homeViewModel;
    private UserViewModel userViewModel;
    private MediaRepository mediaRepository;
    private Media media;
    private boolean isPrenium=false;
    private boolean isPre=false;
    private Episode firstEpisode;

    private List<String> seriesPoster = Arrays.asList(
            "/zvEHDQsiTNMYdp1jppXZKmYmXLO.jpg",
            "/vAkV5ZpmuAhcwAfmqFFMuQsSSFk.jpg",
            "/oag7edI9flSMawmNySEiSEJAbrf.jpg"
    );
    private List<String> moviesPoster = Arrays.asList(
            "/8go3YE9sBMQaCXEx23j6BAfeuxd.jpg",
            "/7DIrtrRgZWWO8jXklLXkkivqpBG.jpg",
            "/84MHN3JvOV4ORHgELrQM6SBlhdB.jpg"
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity(), new HomeViewModelFactory(requireContext()))
                .get(HomeViewModel.class);
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(requireContext());
            }
        }).get(UserViewModel.class);
        mediaRepository = new MediaRepository(requireContext());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Các nút trên header
        binding.btnSeries.setOnClickListener(v -> toggleSelection(binding.btnSeries));
        binding.btnMovies.setOnClickListener(v -> toggleSelection(binding.btnMovies));
        binding.btnGenres.setOnClickListener(v -> toggleSelection(binding.btnGenres));
        binding.cancelAction.setOnClickListener(v -> resetSelection());
        loadButtonState(); // Load lại trạng thái khi chọn

        loadHomeMovie();
        loadMovieByGenres();
        loadTop10Series();
        loadTop10Movies();
        loadPanelMedia();

        // Theo dõi trạng thái loading của data
        loading();

        return root;
    }

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
                        if (userId != null) {
                            homeViewModel.getPanelMedia().observe(getViewLifecycleOwner(), panelMedia -> {
                                userViewModel.checkMediaInWatchList(userId, panelMedia.getId(), null);
                                userViewModel.getIsInWatchList().observe(getViewLifecycleOwner(), isInWatchList -> {
                                    if (isInWatchList != null) {
                                        binding.buttonAdd.setIcon(ContextCompat.getDrawable(requireContext(), isInWatchList ? R.drawable.ic_added : R.drawable.ic_add));
                                    }
                                });
                            });
                        }
                    });
                }
            });

    private void loadPanelMedia() {
        homeViewModel.getPanelMedia().observe(getViewLifecycleOwner(), panelMedia -> {
            if (panelMedia != null) {
                Glide.with(requireContext())
                        .load("https://image.tmdb.org/t/p/w500" + panelMedia.getPosterPath())
                        .placeholder(R.drawable.ic_info)
                        .into(binding.posterImage);

                binding.panel.setOnClickListener(v -> {
                    if ("movie".equals(panelMedia.getType())) {
                        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
                        intent.putExtra("media_id", panelMedia.getId());
                        launcher.launch(intent);
                    } else {
                        Intent intent = new Intent(requireContext(), TvSeriesDetailActivity.class);
                        intent.putExtra("media_id", panelMedia.getId());
                        launcher.launch(intent);
                    }
                });

                binding.buttonPlay.setOnClickListener(v -> {
                    if (panelMedia.getIsPrenium() == 1) {
                        isPre = true;
                    } else {
                        isPre = false;
                    }

                    Long mediaId = panelMedia.getId();
                    if (Objects.equals(panelMedia.getType(), "tv_series")) {
                        getEpisode(mediaId);
                    } else {
                        playFullScreenVideo(mediaId);
                    }
                });

                userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
                    if (userId != null && !userId.isEmpty()) {
                        userViewModel.checkMediaInWatchList(userId, panelMedia.getId(), null);
                        userViewModel.getIsInWatchList().observe(getViewLifecycleOwner(), isInWatchList -> {
                            if (isInWatchList != null) {
                                int iconRes = isInWatchList ? R.drawable.ic_added : R.drawable.ic_add;
                                binding.buttonAdd.setIcon(ContextCompat.getDrawable(requireContext(), iconRes));
                                binding.buttonAdd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!isInWatchList) {
                                            mediaRepository.addToWatchList(userId, new AddToWatchListRequest(panelMedia.getId(), "movie"));
                                            mediaRepository.addToWatchList(userId, new AddToWatchListRequest(panelMedia.getId(), "tv_series"));
                                            userViewModel.setIsInWatchList(true);
                                            binding.buttonAdd.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_added));
                                        } else {
                                            mediaRepository.removeMediaFromWatchList(userId, panelMedia.getId());
                                            userViewModel.setIsInWatchList(false);
                                            binding.buttonAdd.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_add));
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        binding.buttonAdd.setVisibility(View.GONE);
                        binding.buttonPlay.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void loadTop10Series() {
        top10SeriesAdapter = new MediaAdapter(null, MediaAdapter.TYPE_TOP_10);
        binding.rcvTop10Series.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvTop10Series.setAdapter(top10SeriesAdapter);
        homeViewModel.get10Series().observe(getViewLifecycleOwner(), series -> {
            Log.d("HomeFragment", "Observed series: " + series);
            if (series != null) {
                top10SeriesAdapter.setMedia(series);
            }
        });
    }

    private void loadTop10Movies() {
        top10MoviesAdapter = new MediaAdapter(null, MediaAdapter.TYPE_TOP_10);
        binding.rcvTop10Movies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvTop10Movies.setAdapter(top10MoviesAdapter);
        homeViewModel.get10Movies().observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                top10MoviesAdapter.setMedia(movie);
            }
        });
    }

    private void loadMovieByGenres() {
        moviesByGenreAdapter = new MediaAdapter(null, MediaAdapter.TYPE_NORMAL);
        binding.rcvMoviesByGenres.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rcvMoviesByGenres.setAdapter(moviesByGenreAdapter);

        homeViewModel.getSelectedMoviePoster().observe(getViewLifecycleOwner(), posterPath -> {
            if (posterPath != null && !posterPath.isEmpty()) {
                Glide.with(requireContext())
                        .load("https://image.tmdb.org/t/p/w500" + posterPath) // API TMDB cung cấp ảnh
                        .placeholder(R.drawable.ic_info) // Ảnh chờ nếu API chưa tải xong
                        .into(binding.posterImage);
            }
        });
        homeViewModel.getSelectedGenre().observe(getViewLifecycleOwner(), genreName -> {
            if (genreName != null) {
                binding.btnGenres.setText(genreName);
            }
        });
        homeViewModel.getMoviesByGenre().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                binding.rcvMoviesByGenres.setVisibility(View.VISIBLE);
                binding.rcvGenresContainer.setVisibility(View.GONE);
                moviesByGenreAdapter.setMedia(movies);
            }
        });
    }

    private void loadHomeMovie() {
        genreAdapter = new GenreAdapter();
        binding.rcvGenresContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvGenresContainer.setAdapter(genreAdapter);
        //binding.rcvGenresContainer.setItemAnimator(new DefaultItemAnimator());

        homeViewModel.getMedia().observe(getViewLifecycleOwner(), media -> {
            if (media != null /*&& media.size() == homeViewModel.getGenres().getValue().size()*/) {
                genreAdapter.setGenresForMedia(homeViewModel.getGenres().getValue(), media);
            }
        });
    }

    private void toggleSelection(MaterialButton selectedButton) {
        // Hiển thị nút Cancel
        //binding.cancelAction.setVisibility(View.VISIBLE);
        homeViewModel.setCancelActionState(true);

        if (selectedButton == binding.btnSeries) {
            //setPoster(0);
            binding.btnMovies.setVisibility(View.GONE);
            binding.top10movies.setVisibility(View.GONE);
            if (Boolean.FALSE.equals(homeViewModel.getIsSeriesSelected().getValue()))
                homeViewModel.fetchGenresForSeries(); // Gọi API Series
            homeViewModel.setSeriesSelected(true);
            homeViewModel.setPanelMedia(homeViewModel.get10Series().getValue().get(0));
            //homeViewModel.setMedia(new HashMap<>());
        } else if (selectedButton == binding.btnMovies) {
            //setPoster(1);
            binding.btnSeries.setVisibility(View.GONE);
            binding.top10series.setVisibility(View.GONE);
            if (Boolean.FALSE.equals(homeViewModel.getIsMoviesSelected().getValue()))
                homeViewModel.fetchGenresForMovies(); // Gọi API Movies
            homeViewModel.setMoviesSelected(true);
            homeViewModel.setPanelMedia(homeViewModel.get10Movies().getValue().get(0));
        } else {
            binding.btnSeries.setVisibility(View.GONE);
            binding.btnMovies.setVisibility(View.GONE);
            homeViewModel.setGenresSelected(true);
            GenresItemListDialogFragment.newInstance(this).show(getParentFragmentManager(), "GenresDialog");
        }
    }

    private void setPoster(int type) {
        List<String> posters = type == 0 ? seriesPoster : moviesPoster;
        int randomIndex = (int) (Math.random() * posters.size());
        homeViewModel.setSelectedMoviePoster(posters.get(randomIndex));

        homeViewModel.getSelectedMoviePoster().observe(getViewLifecycleOwner(), posterPath -> {
            if (posterPath != null && !posterPath.isEmpty()) {
                Glide.with(requireContext())
                        .load("https://image.tmdb.org/t/p/w500" + posterPath) // API TMDB cung cấp ảnh
                        .placeholder(R.drawable.ic_info) // Ảnh chờ nếu API chưa tải xong
                        .into(binding.posterImage);
            }
        });
    }

    private void resetSelection() {
        // Ẩn nút Cancel
        homeViewModel.setCancelActionState(false);
        binding.btnSeries.setVisibility(View.VISIBLE);
        binding.btnMovies.setVisibility(View.VISIBLE);
        binding.rcvGenresContainer.setVisibility(View.VISIBLE);
        binding.rcvMoviesByGenres.setVisibility(View.GONE);
        binding.btnGenres.setText("Thể loại");

        // Đặt trạng thái cho button
        homeViewModel.setSeriesSelected(false);
        homeViewModel.setMoviesSelected(false);
        homeViewModel.setGenresSelected(false);

        // Gọi lại API mặc định
        homeViewModel.fetchGenres();
        homeViewModel.setPanelMedia(homeViewModel.get10Series().getValue().get(0));
    }

    private void loading() {
        homeViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentLayout.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.contentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadButtonState() {
        homeViewModel.getIsSeriesSelected().observe(getViewLifecycleOwner(), isSelected -> {
            if (isSelected) {
                binding.btnMovies.setVisibility(View.GONE);
                binding.btnSeries.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selected_button));
            } else {
                binding.btnSeries.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.transparent));
            }
        });
        homeViewModel.getIsMoviesSelected().observe(getViewLifecycleOwner(), isSelected -> {
            if (isSelected) {
                binding.btnSeries.setVisibility(View.GONE);
                binding.btnMovies.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selected_button));
            } else {
                binding.btnMovies.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.transparent));
            }
        });
        homeViewModel.getIsGenresSelected().observe(getViewLifecycleOwner(), isSelected -> {
            if (isSelected) {
                binding.btnSeries.setVisibility(View.GONE);
                binding.btnMovies.setVisibility(View.GONE);
                binding.btnGenres.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selected_button));
            } else {
                binding.btnGenres.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.transparent));
            }
        });
        homeViewModel.getCancelActionState().observe(getViewLifecycleOwner(), state -> {
            if (state) {
                binding.cancelAction.setVisibility(View.VISIBLE);
            } else {
                binding.cancelAction.setVisibility(View.GONE);
                binding.top10series.setVisibility(View.VISIBLE);
                binding.top10movies.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideTop10() {
        if (binding != null) {
            binding.top10series.setVisibility(View.GONE);
            binding.top10movies.setVisibility(View.GONE);
        }
    }

    private void getEpisode(long id) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPreniumDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
