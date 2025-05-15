package com.netflixcloneui.ui.mynetflix;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.ui.NotificationActivity;
import com.netflixcloneui.ui.PaymentPackageActivity;
import com.netflixcloneui.ui.WatchListActivity;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MyNetflixFragment extends Fragment {
    private FragmentMyNetflixBinding binding;
    private MyNetflixViewModel myNetflixViewModel;
    private static UserViewModel userViewModel;
    private MediaAdapter favoriteAdapter;
    private MediaAdapter myListAdapter;
    private String userId;
    public static boolean isPremium = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myNetflixViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyNetflixViewModel(requireContext());
            }
        }).get(MyNetflixViewModel.class);
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(requireContext());
            }
        }).get(UserViewModel.class);

        binding = FragmentMyNetflixBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.userId = user.getId();
                binding.tvName.setText(user.getName());
                myNetflixViewModel.fetchUserLikeList(user.getId());
                myNetflixViewModel.fetchUserWatchList(user.getId());
                int resId = getResources().getIdentifier(user.getImage(), "drawable", getContext().getPackageName());
                if (resId != 0) {
                    binding.ivAvatar.setImageResource(resId);
                }

                if (user.getEndDate() != null) {
                    DateTimeFormatter formatter = null;
                    LocalDateTime endDateTime;
                    LocalDateTime now;
                    String result = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"); // Định dạng ISO hoặc bạn tùy chỉnh theo định dạng bạn lưu
                        endDateTime = LocalDateTime.parse(user.getEndDate(), formatter);
                        now = LocalDateTime.now();

                        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), endDateTime.toLocalDate());

                        if (daysBetween > 0) {
                            isPremium = true;
                            result = "Còn " + daysBetween + " ngày nữa đến hạn";
                        } else if (daysBetween == 0) {
                            result = "Hết hạn hôm nay";
                        } else {
                            result = "Đã quá hạn " + Math.abs(daysBetween) + " ngày";
                        }
                    }
                    binding.tvPremium.setText("Bạn đã tham gia Premium");
                    binding.tvPremium.setTextSize(20);
                    binding.tvPremiumDetail.setText(result);
                }
            }
        });

        loadFavoriteList();
        premium();
        loadMyList();
        addItemToActionBar();
        handleButtonWatchList();
        binding.btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        return root;
    }

    public static void setUser(UserResponse user) {
        userViewModel.setUser(user);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi fragment được hiển thị lại
        if (userId != null) {
            myNetflixViewModel.fetchUserLikeList(userId);
            myNetflixViewModel.fetchUserWatchList(userId);
        }
    }

    private void handleButtonWatchList() {
        myNetflixViewModel.getUserMovieList().observe(getViewLifecycleOwner(), watchList -> {
            if (watchList != null) {
                binding.btnWatchlist.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), WatchListActivity.class);
                    intent.putExtra("media_list", (Serializable)watchList);
                    startActivity(intent);
                });
            }
        });
    }

    private void premium() {
        binding.premium.setOnClickListener(v -> openPaymentPackage());
    }

    private void openPaymentPackage() {
        Intent intent = new Intent(getContext(), PaymentPackageActivity.class);
        startActivity(intent);
    }

    private void loadMyList() {
        // Adapter cho danh sách phim thông thường - dùng layout mặc định
        myListAdapter = new MediaAdapter(null, MediaAdapter.TYPE_NORMAL);
        binding.rcvMyList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvMyList.setAdapter(myListAdapter);

        myNetflixViewModel.getUserMovieList().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                binding.layoutLikeList.setVisibility(View.VISIBLE);
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
            if (movies != null &&!movies.isEmpty()) {
                binding.layoutWatchList.setVisibility(View.VISIBLE);
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
}
