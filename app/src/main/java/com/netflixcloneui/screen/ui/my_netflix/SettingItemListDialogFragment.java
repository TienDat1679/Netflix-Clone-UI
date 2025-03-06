package com.netflixcloneui.screen.ui.my_netflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netflixcloneui.R;
import com.netflixcloneui.databinding.FragmentItemListDialogListDialogItemBinding;
import com.netflixcloneui.databinding.FragmentItemListDialogListDialogBinding;
import com.netflixcloneui.screen.auth.LoginActivity;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     SettingItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class SettingItemListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentItemListDialogListDialogBinding binding;

    // TODO: Customize parameters
    public static SettingItemListDialogFragment newInstance(int itemCount) {
        final SettingItemListDialogFragment fragment = new SettingItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SettingItemAdapter(/*getArguments().getInt(ARG_ITEM_COUNT)*/));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final ImageView icon;

        ViewHolder(FragmentItemListDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.itemText;
            icon = binding.itemIcon;
        }
    }

    private class SettingItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        // Danh sách tiêu đề và icon của mỗi mục
        private final String[] settingOptions = {
                "Quản lý hồ sơ", "Cài đặt ứng dụng", "Tài khoản", "Trợ giúp", "Đăng xuất"
        };

        private final int[] settingIcons = {
                R.drawable.ic_edit,   // Icon tài khoản
                R.drawable.ic_settings_outline,  // Icon thông báo
                R.drawable.ic_user,  // Icon ngôn ngữ
                R.drawable.ic_help,  // Icon trợ giúp
                R.drawable.ic_logout // Icon đăng xuất
        };

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentItemListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(settingOptions[position]);
            holder.icon.setImageResource(settingIcons[position]);

            holder.itemView.setOnClickListener(v -> {
                if (settingOptions[position].equals("Đăng xuất")) { // Sửa so sánh String
                    logout();
                } else {
                    Toast.makeText(getContext(), "Bạn chọn: " + settingOptions[position], Toast.LENGTH_SHORT).show();
                }
                dismiss(); // Đóng BottomSheet sau khi chọn
            });
        }

        private void logout() {
            Toast.makeText(requireContext(), "Bạn đã đăng xuất.", Toast.LENGTH_SHORT).show();

            // Xóa JWT token khỏi SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("jwt_token");
            editor.apply();

            // Chuyển sang màn hình đăng nhập
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);

            // Đóng BottomSheet
            dismiss();
        }

        @Override
        public int getItemCount() {
            return settingOptions.length;
        }
    }

}