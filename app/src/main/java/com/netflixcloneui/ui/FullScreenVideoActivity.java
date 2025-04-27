package com.netflixcloneui.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.netflixcloneui.R;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.request.PlaybackProgressRequest;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenVideoActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private ImageButton closeButton;

    private Handler handler;
    private Runnable updatePositionRunnable;
    private boolean isUpdating = false;
    private static final long START_UPDATE_AFTER_MS = 5 * 60 * 1000; // 5 phút = 300.000 ms
    private static final long UPDATE_INTERVAL_MS = 15 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        long postiton = (long) getIntent().getLongExtra("positon",-1);
        long mediaId = (long) getIntent().getLongExtra("VIDEO_ID",-1);
        playerView = findViewById(R.id.playerView);
        closeButton = findViewById(R.id.closeButton);

        // Khởi tạo ExoPlayer (như trước)
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Tạo và chuẩn bị video
        Uri videoUri = Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        if(postiton !=0 )
        {
            player.seekTo(postiton);
        }
        else {
            player.seekTo(0);
        }
        player.play();
        startUpdatingPlaybackProgress(mediaId);
        // Đóng video khi nhấn nút
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                finish();  // Hoặc bạn có thể dùng `onBackPressed()` để quay lại Activity trước
            }
        });
    }
    private void startUpdatingPlaybackProgress(Long mediaId) {
        handler = new Handler();
        updatePositionRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    long currentPosition = player.getCurrentPosition();
                    if (currentPosition >= START_UPDATE_AFTER_MS) {
                        sendPlaybackProgress(currentPosition, mediaId); // ⭐ Gửi API lên server
                        handler.postDelayed(this, UPDATE_INTERVAL_MS); // Lặp lại sau mỗi 15 giây
                    } else {
                        // Chưa đủ 5 phút thì kiểm tra lại sau 5 giây
                        handler.postDelayed(this, 5000);
                    }
                } else {
                    // Nếu player dừng thì ngưng handler luôn cho đỡ tốn tài nguyên
                    handler.postDelayed(this, 5000);
                }
            }
        };
        handler.post(updatePositionRunnable);
        isUpdating = true;
    }


    private void stopUpdatingPlaybackProgress() {
        if (handler != null && updatePositionRunnable != null) {
            handler.removeCallbacks(updatePositionRunnable);
            isUpdating = false;
        }
    }

    private void sendPlaybackProgress(long currentPosition,Long mediaId) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        PlaybackProgressRequest request = new PlaybackProgressRequest();
        request.setMovieId(mediaId);
        request.setPosition(currentPosition);

        Call<Void> call = apiService.savePlaybackProgress(mediaId,currentPosition);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Thành công -> không cần làm gì
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}

