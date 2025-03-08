package com.netflixcloneui.screen.ui.my_netflix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.databinding.FragmentMyNetflixBinding;

public class MyNetflixFragment extends Fragment {

    private FragmentMyNetflixBinding binding;
    private MyNetflixViewModel myNetflixViewModel;
    private MediaAdapter favoriteAdapter;
    private MediaAdapter myListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myNetflixViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyNetflixViewModel(requireContext());
            }
        }).get(MyNetflixViewModel.class);

        binding = FragmentMyNetflixBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadFavoriteList();

        loadMyList();

        addItemToActionBar();

        return root;
    }

    private void loadMyList() {
        // Adapter cho danh sách phim thông thường - dùng layout mặc định
        myListAdapter = new MediaAdapter(null, MediaAdapter.TYPE_NORMAL);
        binding.rcvMyList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rcvMyList.setAdapter(myListAdapter);

        myNetflixViewModel.getUserMovieList().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                myListAdapter.setMedia(movies);
            }
        });
    }

    private void loadFavoriteList() {
        // Adapter cho danh sách yêu thích (Favorite) - dùng layout khác
        favoriteAdapter = new MediaAdapter(null, MediaAdapter.TYPE_FAVORITE);
        binding.rcvMyFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvMyFavorite.setAdapter(favoriteAdapter);

        myNetflixViewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                favoriteAdapter.setMedia(movies);
            }
        });
    }

    private void addItemToActionBar() {
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

                if (id == R.id.action_setting) {
                    SettingItemListDialogFragment.newInstance(5).show(getParentFragmentManager(), "dialog");
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void openMovieDetail(Movie movie) {
//        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
//        intent.putExtra("movie_id", movie.getId()); // Truyền ID phim
//        startActivity(intent);
//    }
}
