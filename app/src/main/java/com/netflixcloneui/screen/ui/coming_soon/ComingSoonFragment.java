package com.netflixcloneui.screen.ui.coming_soon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.adapter.ComingSoonAdapter;
import com.netflixcloneui.databinding.FragmentComingSoonBinding;

public class ComingSoonFragment extends Fragment {

    private FragmentComingSoonBinding binding;
    private ComingSoonViewModel comingSoonViewModel;
    private ComingSoonAdapter comingSoonAdapter;

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

        // Cấu hình RecyclerView
        comingSoonAdapter = new ComingSoonAdapter();
        binding.rcvComingSoon.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvComingSoon.setAdapter(comingSoonAdapter);

        // Hiển thị ProgressBar khi tải dữ liệu
        comingSoonViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.rcvComingSoon.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        // Quan sát dữ liệu phim
        comingSoonViewModel.getComingSoonMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                comingSoonAdapter.setMovies(movies);
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