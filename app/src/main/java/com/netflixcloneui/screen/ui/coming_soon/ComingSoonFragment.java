package com.netflixcloneui.screen.ui.coming_soon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.adapter.ComingSoonAdapter;
import com.netflixcloneui.adapter.HotAdapter;
import com.netflixcloneui.databinding.FragmentComingSoonBinding;

public class ComingSoonFragment extends Fragment {

    private FragmentComingSoonBinding binding;
    private ComingSoonViewModel comingSoonViewModel;
    private ComingSoonAdapter comingSoonAdapter;
    private HotAdapter hotAdapter, topMoviesAdapter, topSeriesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        comingSoonViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ComingSoonViewModel(requireContext());
            }
        }).get(ComingSoonViewModel.class);

        binding = FragmentComingSoonBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnComingSoon.setOnClickListener(v -> scrollToRecyclerView(binding.rcvComingSoon));
        binding.btnHot.setOnClickListener(v -> scrollToRecyclerView(binding.rcvHot));
        binding.btnTopSeries.setOnClickListener(v -> scrollToRecyclerView(binding.rcvTopSeries));
        binding.btnTopMovies.setOnClickListener(v -> scrollToRecyclerView(binding.rcvTopMovies));

        loadComingSoon();
        loadHot();
        loadTopSeries();
        loadTopMovies();

        // Hiển thị ProgressBar khi tải dữ liệu
        loading();

        return root;
    }

    private void loadHot() {
        // Cấu hình RecyclerView
        hotAdapter = new HotAdapter(HotAdapter.TYPE_HOT);
        binding.rcvHot.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvHot.setAdapter(hotAdapter);
        // Quan sát dữ liệu phim
        comingSoonViewModel.getHotSeriesMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                hotAdapter.setHotMedia(movies);
            }
        });
    }

    private void loadTopSeries() {
        // Cấu hình RecyclerView
        topSeriesAdapter = new HotAdapter(HotAdapter.TYPE_TOP_SERIES);
        binding.rcvTopSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvTopSeries.setAdapter(topSeriesAdapter);
        // Quan sát dữ liệu phim
        comingSoonViewModel.getTopSeries().observe(getViewLifecycleOwner(), series -> {
            if (series != null) {
                topSeriesAdapter.setSeries(series);
            }
        });
    }

    private void loadTopMovies() {
        // Cấu hình RecyclerView
        topMoviesAdapter = new HotAdapter(HotAdapter.TYPE_TOP_MOVIES);
        binding.rcvTopMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvTopMovies.setAdapter(topMoviesAdapter);
        // Quan sát dữ liệu phim
        comingSoonViewModel.getTopMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                topMoviesAdapter.setMovies(movies);
            }
        });
    }

    private void loadComingSoon() {
        // Cấu hình RecyclerView
        comingSoonAdapter = new ComingSoonAdapter();
        binding.rcvComingSoon.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvComingSoon.setAdapter(comingSoonAdapter);
        // Quan sát dữ liệu phim
        comingSoonViewModel.getComingSoonMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                comingSoonAdapter.setMedia(movies);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}