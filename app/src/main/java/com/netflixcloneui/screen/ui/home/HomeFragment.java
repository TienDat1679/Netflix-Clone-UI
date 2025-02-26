package com.netflixcloneui.screen.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.MovieDetailActivity;
import com.netflixcloneui.adapter.GenreAdapter;
import com.netflixcloneui.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GenreAdapter genreAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomeViewModel(requireContext());
            }
        }).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Xử lý sự kiện khi click vào phim
        genreAdapter = new GenreAdapter(movie -> {
            // Hiển thị thông báo hoặc chuyển sang màn hình chi tiết
            Toast.makeText(getContext(), "Clicked: " + movie.getTitle(), Toast.LENGTH_SHORT).show();

            // Mở màn hình chi tiết phim
            Intent intent = new Intent(getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId()); // Truyền ID phim sang màn hình khác
            startActivity(intent);
        });

        // Cấu hình RecyclerView
        //genreAdapter = new GenreAdapter();
        binding.rcvGenresContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvGenresContainer.setAdapter(genreAdapter);

        // Theo dõi trạng thái loading
        homeViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentLayout.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.contentLayout.setVisibility(View.VISIBLE);
            }
        });

        // Quan sát dữ liệu từ ViewModel
        homeViewModel.getMovies().observe(getViewLifecycleOwner(), moviesMap -> {
            if (moviesMap != null && !moviesMap.isEmpty()) {
                genreAdapter.setGenres(homeViewModel.getGenres().getValue(), moviesMap);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}