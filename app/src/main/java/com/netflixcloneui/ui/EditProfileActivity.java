package com.netflixcloneui.ui;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.netflixcloneui.R;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.databinding.ActivityEditProfileBinding;
import com.netflixcloneui.model.request.UserUpdateRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.ui.mynetflix.MyNetflixFragment;
import com.netflixcloneui.viewmodel.UserViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserViewModel(getApplicationContext());
            }
        }).get(UserViewModel.class);

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                this.userId = user.getId();
                binding.etName.setText(user.getName());
                binding.etDob.setText(user.getDob());
                binding.tvImage.setText(user.getImage());
                int resId = getResources().getIdentifier(user.getImage(), "drawable", getPackageName());
                if (resId != 0) {
                    binding.ivAvatar.setImageResource(resId);
                }
            }
        });

        binding.ivEdit.setOnClickListener(v -> {
            EditAvatarBottomSheetFragment bottomSheet = new EditAvatarBottomSheetFragment();
            bottomSheet.setOnAvatarSelectedListener(avatarName -> {
                int resId = getResources().getIdentifier(avatarName, "drawable", getPackageName());
                if (resId != 0) {
                    binding.ivAvatar.setImageResource(resId);
                }
                binding.tvImage.setText(avatarName);
            });
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        binding.etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view1, year, month, dayOfMonth) -> {
                        // Định dạng yyyy-MM-dd
                        String dob = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        binding.etDob.setText(dob);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String dob = binding.etDob.getText().toString().trim();
            String image = binding.tvImage.getText().toString().trim();

            if (name.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            LocalDate date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    // Kiểm tra và chuyển đổi chuỗi ngày sinh thành LocalDate
                    date = LocalDate.parse(dob); // Mặc định định dạng là yyyy-MM-dd
                } catch (DateTimeParseException e) {
                    Toast.makeText(this, "Ngày sinh không đúng định dạng (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // Gửi request cập nhật
            updateUserProfile(name, dob, image);
        });

        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void updateUserProfile(String name, String dob, String image) {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<ApiResponse<UserResponse>> call = apiService.updateUser(userId, new UserUpdateRequest(null, name, dob, null, image));
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyNetflixFragment.setUser(response.body().getResult());
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {

            }
        });
    }
}