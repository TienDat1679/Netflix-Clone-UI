package com.netflixcloneui.screen.ui.my_netflix;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.MovieDetailActivity;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.MovieAdapter;
import com.netflixcloneui.databinding.FragmentMyNetflixBinding;
import com.netflixcloneui.model.Movie;

public class MyNetflixFragment extends Fragment {

    private FragmentMyNetflixBinding binding;
    private MyNetflixViewModel myNetflixViewModel;
    private MovieAdapter favoriteAdapter;
    private MovieAdapter myListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyNetflixViewModel myNetflixViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyNetflixViewModel(requireContext());
            }
        }).get(MyNetflixViewModel.class);

        binding = FragmentMyNetflixBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Adapter cho danh sách yêu thích (Favorite) - dùng layout khác
        favoriteAdapter = new MovieAdapter(null, true, movie -> {
            Toast.makeText(getContext(), "Bạn đã chọn: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            openMovieDetail(movie);
        });

        // Adapter cho danh sách phim thông thường - dùng layout mặc định
        myListAdapter = new MovieAdapter(null, false, movie -> {
            Toast.makeText(getContext(), "Bạn đã chọn: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            openMovieDetail(movie);
        });

        // Cấu hình RecyclerView
        binding.rcvMyFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvMyFavorite.setAdapter(favoriteAdapter);

        binding.rcvMyList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rcvMyList.setAdapter(myListAdapter);

        // Quan sát dữ liệu từ ViewModel
        myNetflixViewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                favoriteAdapter.setMovies(movies);
            }
        });

        myNetflixViewModel.getUserMovieList().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                myListAdapter.setMovies(movies);
            }
        });

        // Thêm MenuProvider để quản lý menu
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Gắn menu vào ActionBar
                menuInflater.inflate(R.menu.my_netflix_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Xử lý sự kiện khi nhấn vào các nút
                int id = menuItem.getItemId();

                if (id == R.id.action_info) {
                    Toast.makeText(requireContext(), "Custom button clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Hàm mở chi tiết phim khi click vào item
    private void openMovieDetail(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movie_id", movie.getId()); // Truyền ID phim
        startActivity(intent);
    }
}