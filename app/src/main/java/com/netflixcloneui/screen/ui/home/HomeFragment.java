package com.netflixcloneui.screen.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.netflixcloneui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.GenreAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.databinding.FragmentHomeBinding;
import com.netflixcloneui.model.Movie;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GenreAdapter genreAdapter;
    private MediaAdapter moviesByGenreAdapter;
    //private MovieAdapter.OnMovieClickListener movieClickListener;
    private HomeViewModel homeViewModel;
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

        // Theo dõi trạng thái loading của data
        loading();

        return root;
    }

    private void openMovieDetail(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movie_id", movie.getId()); // Truyền ID phim
        startActivity(intent);
    }

    private void loadMovieByGenres() {
        moviesByGenreAdapter = new MediaAdapter(null, false);
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

        // Cấu hình RecyclerView
        binding.rcvGenresContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvGenresContainer.setAdapter(genreAdapter);
        //binding.rcvGenresContainer.setItemAnimator(new DefaultItemAnimator());

        homeViewModel.getMedia().observe(getViewLifecycleOwner(), media -> {
            if (media != null) {
                genreAdapter.setGenresForMedia(homeViewModel.getGenres().getValue(), media);
            }
        });
    }

    private void toggleSelection(MaterialButton selectedButton) {
        // Hiển thị nút Cancel
        //binding.cancelAction.setVisibility(View.VISIBLE);
        homeViewModel.setCancelActionState(true);

        if (selectedButton == binding.btnSeries) {
            setPoster(0);
            binding.btnMovies.setVisibility(View.GONE);
            homeViewModel.setSeriesSelected(true);
            homeViewModel.fetchGenresForSeries(); // Gọi API Series
        } else if (selectedButton == binding.btnMovies) {
            setPoster(1);
            binding.btnSeries.setVisibility(View.GONE);
            homeViewModel.setMoviesSelected(true);
            homeViewModel.fetchGenresForMovies(); // Gọi API Movies
        } else {
            binding.btnSeries.setVisibility(View.GONE);
            binding.btnMovies.setVisibility(View.GONE);
            homeViewModel.setGenresSelected(true);
            GenresItemListDialogFragment.newInstance().show(getParentFragmentManager(), "GenresDialog");
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
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}