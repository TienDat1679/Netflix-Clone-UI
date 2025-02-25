package com.netflixcloneui.screen.ui.my_netflix;

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
import androidx.lifecycle.ViewModelProvider;

import com.netflixcloneui.R;
import com.netflixcloneui.databinding.FragmentMyNetflixBinding;

public class MyNetflixFragment extends Fragment {

    private FragmentMyNetflixBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyNetflixViewModel myNetflixViewModel =
                new ViewModelProvider(this).get(MyNetflixViewModel.class);

        binding = FragmentMyNetflixBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMyNetflix;
        myNetflixViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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

                if (id == R.id.action_search) {
                    Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_info) {
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
}