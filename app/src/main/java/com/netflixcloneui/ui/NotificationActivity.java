package com.netflixcloneui.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.NotificationAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.databinding.ActivityNotificationBinding;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.response.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadNotification();

        binding.imgBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadNotification() {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<ApiResponse<List<Media>>> call = apiService.getUserInbox();
        call.enqueue(new Callback<ApiResponse<List<Media>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Media>>> call, Response<ApiResponse<List<Media>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getResult().isEmpty())
                        binding.tv.setVisibility(View.GONE);

                    List<Media> mediaList = response.body().getResult();
                    notificationAdapter = new NotificationAdapter();
                    notificationAdapter.setMediaList(mediaList);
                    binding.rcvNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                    binding.rcvNotification.setAdapter(notificationAdapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Media>>> call, Throwable t) {

            }
        });
    }
}