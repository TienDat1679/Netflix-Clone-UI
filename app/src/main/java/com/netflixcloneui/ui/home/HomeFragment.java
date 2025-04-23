package com.netflixcloneui.ui.home;

import android.app.Activity;
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
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.request.AddToWatchListRequest;
import com.netflixcloneui.model.request.LikeRequest;
import com.netflixcloneui.ui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.GenreAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.databinding.FragmentHomeBinding;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.ui.TvSeriesDetailActivity;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GenreAdapter genreAdapter;
    private MediaAdapter moviesByGenreAdapter, top10SeriesAdapter, top10MoviesAdapter;
    //private MovieAdapter.OnMovieClickListener movieClickListener;
    private HomeViewModel homeViewModel;
    private UserViewModel userViewModel;
    private MediaRepository mediaRepository;
    private Media media;

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

                userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
                    if (userId != null) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
