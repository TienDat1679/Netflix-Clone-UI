package com.netflixcloneui.screen.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netflixcloneui.databinding.FragmentGenresListDialogListDialogItemBinding;
import com.netflixcloneui.databinding.FragmentGenresListDialogListDialogBinding;
import com.netflixcloneui.model.Genre;

import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     GenresItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class GenresItemListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentGenresListDialogListDialogBinding binding;
    private HomeViewModel homeViewModel;

    // TODO: Customize parameters
    public static GenresItemListDialogFragment newInstance(/*int itemCount*/) {
//        final GenresItemListDialogFragment fragment = new GenresItemListDialogFragment();
//        final Bundle args = new Bundle();
//        args.putInt(ARG_ITEM_COUNT, itemCount);
//        fragment.setArguments(args);
//        return fragment;
        return new GenresItemListDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGenresListDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        final RecyclerView recyclerView = (RecyclerView) view;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new GenresItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity(), new HomeViewModelFactory(requireContext())).get(HomeViewModel.class);

        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null && !genres.isEmpty()) {
                binding.list.setAdapter(new GenresItemAdapter(genres));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentGenresListDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }

    private class GenresItemAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<Genre> genres;

        GenresItemAdapter(List<Genre> genres) {
            this.genres = genres;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentGenresListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Genre genre = genres.get(position);
            holder.text.setText(genre.getName());

            // Xử lý khi click vào một thể loại
            holder.itemView.setOnClickListener(v -> {
                homeViewModel.setSelectedGenre(genre.getName()); // Cập nhật thể loại vào ViewModel
                homeViewModel.fetchMediaByGenre(genre.getId());
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return genres.size();
        }
    }
}