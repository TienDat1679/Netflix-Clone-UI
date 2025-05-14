package com.netflixcloneui.ui;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.RecommendAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.PlayBackResponse;
import com.netflixcloneui.model.response.UserResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private ImageView micIcon;
    private boolean isRecording = false;
    private AnimatorSet animatorSet;
    private EditText edtSearch;
    private ImageView imgBack, imgVoice;
    private RecyclerView rcvRecommend;
    private TextView noResult;
    private TextView text;
    private MediaRepository mediaRepository;
    private RecommendAdapter recommendAdapter;
    private Episode firstEpisode;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isPrenium=false;
    private boolean isPre=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Ánh xạ view
        edtSearch = findViewById(R.id.edt_search);
        rcvRecommend = findViewById(R.id.rcv_recommend);
        imgBack = findViewById(R.id.img_back);
        text = findViewById(R.id.text);
        noResult = findViewById(R.id.tv_sorry);
        imgVoice = findViewById(R.id.img_voice);
        animation();
        mediaRepository = new MediaRepository(this);
        recommendAdapter = new RecommendAdapter(new RecommendAdapter.onCLickListener() {
            @Override
            public void onItemClick(Media media) {
                if (media.getIsPrenium() == 1) {
                    isPre = true;
                } else {
                    isPre = false;
                }

                Long mediaId = media.getId();
                if (Objects.equals(media.getType(), "tv_series")) {
                    getEpisode(mediaId);
                } else {
                    playFullScreenVideo(mediaId);
                }

            }
        });
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this));
        rcvRecommend.setAdapter(recommendAdapter);

        // Load danh sách gợi ý ban đầu
        loadRecommend();
        checkPrenium();

        imgBack.setOnClickListener(v -> finish());

        // Gắn sự kiện tìm kiếm khi gõ
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    text.setVisibility(View.GONE);
                    searchMovies(s.toString());
                } else if (s.length() == 0) {
                    text.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.GONE);
                    rcvRecommend.setVisibility(View.VISIBLE);
                    loadRecommend();
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            setupSpeechRecognizer();
        }

        imgVoice.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền ghi âm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animation() {
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                AnimatorInflater.loadAnimator(this, R.animator.mic_pulse_scale_x),
                AnimatorInflater.loadAnimator(this, R.animator.mic_pulse_scale_y)
        );
        // Set click listener on microphone icon
        imgVoice.setOnClickListener(v -> {
            isRecording = !isRecording; // Toggle recording state
            if (isRecording) {
                // Change icon color to red
                imgVoice.setColorFilter(Color.RED);
                // Start animation
                animatorSet.start();
                // Here you can add logic for starting the recording
                Toast.makeText(SearchActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
            } else {
                // Reset icon color
                imgVoice.setColorFilter(Color.BLACK);

                // Stop animation
                animatorSet.end();
            }
        });
    }
    private void setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Dịch vụ nhận diện giọng nói không khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "vi-VN");
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                String errorMessage = "Lỗi nhận diện giọng nói";
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "Lỗi âm thanh, kiểm tra micro.";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "Lỗi client, thử lại.";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "Không đủ quyền để sử dụng microphone.";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "Lỗi mạng, thử lại sau.";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "Không có kết quả khớp.";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage = "Mic đang ghi âm vui lòng nói từ khóa cần tìm";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage = "Lỗi từ server.";
                        break;
                    default:
                        errorMessage = "Lỗi không xác định.";
                }
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("SpeechRecognizer", "Lỗi nhận diện: " + error + " - " + errorMessage);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String voiceQuery = matches.get(0);
                    edtSearch.setText(voiceQuery);
                    Log.d("voice",voiceQuery);
                    callSearchByVoiceAPI(voiceQuery);
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }
    private void callSearchByVoiceAPI(String query) {
        searchMovies(query);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupSpeechRecognizer();
        } else {
            Toast.makeText(this, "Không thể sử dụng giọng nói nếu không cấp quyền", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchMovies(String keyword) {
        mediaRepository.searchMedia(keyword, new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    recommendAdapter.setMedia(response.body());
                    noResult.setVisibility(View.GONE);
                    rcvRecommend.setVisibility(View.VISIBLE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                    rcvRecommend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi API: " + t.getMessage());
                noResult.setVisibility(View.VISIBLE);
                rcvRecommend.setVisibility(View.GONE);
            }
        });
    }

    private void loadRecommend() {
        mediaRepository.getTrendingMedia(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendAdapter.setMedia(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi API: " + t.getMessage());
            }
        });
    }

    private void getEpisode(long id) {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    firstEpisode = response.body().get(0);
                    playFullScreenVideo(firstEpisode.getId());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("Tvseries Eps", "API Call failed: " + t.getMessage());
            }
        });
    }

    public void playFullScreenVideo(Long episodeIdOne) {
        Log.d("esp",String.valueOf(episodeIdOne));
        if (getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("jwt_token", null) != null) {
            if(isPre)
            {
                if(isPrenium)
                {
                    ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                    Call<PlayBackResponse> call = apiService.getPlaybackProgress(episodeIdOne); // Không cần chuyển đổi bằng `Long.valueOf()`
                    call.enqueue(new Callback<PlayBackResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                PlayBackResponse PlayBackResponse = response.body();
                                showContinueWatchingDialog(PlayBackResponse.getPosition(),episodeIdOne);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                            Intent intent = new Intent(SearchActivity.this, FullScreenVideoActivity.class);
                            intent.putExtra("VIDEO_ID", episodeIdOne);
                            intent.putExtra("position",0);// Truyền videoId vào Intent
                            startActivity(intent);
                        }
                    });
                }
                else {
                    showPreniumDialog();
                }
            }
            else {
                ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
                Call<PlayBackResponse> call = apiService.getPlaybackProgress(episodeIdOne); // Không cần chuyển đổi bằng `Long.valueOf()`
                call.enqueue(new Callback<PlayBackResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            PlayBackResponse PlayBackResponse = response.body();
                            showContinueWatchingDialog(PlayBackResponse.getPosition(),episodeIdOne);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                        Intent intent = new Intent(SearchActivity.this, FullScreenVideoActivity.class);
                        intent.putExtra("VIDEO_ID", episodeIdOne);
                        intent.putExtra("postion",0);// Truyền videoId vào Intent
                        startActivity(intent);
                    }
                });
            }

        } else {
            TvSeriesDetailActivity.showLoginDialog(this);
        }
    }

    private void showContinueWatchingDialog(Long savedPosition,Long mediaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiếp tục xem?");
        builder.setMessage("Bạn muốn tiếp tục xem từ phút " + (savedPosition / 60000) + " không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            Intent intent = new Intent(SearchActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",savedPosition);// Truyền videoId vào Intent
            startActivity(intent);
        });

        builder.setNegativeButton("Xem lại từ đầu", (dialog, which) -> {
            ApiService apiService = RetrofitClient.getApiService(SearchActivity.this);
            Call<Void> call = apiService.deletePlayback(mediaId);// Không cần chuyển đổi bằng `Long.valueOf()`
            call.enqueue(new Callback<Void>() {
                             @Override
                             public void onResponse(Call<Void> call, Response<Void> response) {

                             }

                             @Override
                             public void onFailure(Call<Void> call, Throwable t) {

                             }
                         }

            );
            Intent intent = new Intent(SearchActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",0);// Truyền videoId vào Intent
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPreniumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phim chỉ dành cho tài khoan prenium");
        builder.setMessage("Hãy trỏe thành thành vien prenium");

        builder.setPositiveButton("Đăng ki prenium", (dialog, which) -> {
            Intent intent = new Intent(SearchActivity.this, PaymentPackageActivity.class);
            startActivity(intent);
        });

        builder.setNegativeButton("Đóng", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkPrenium() {
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());
        Call<ApiResponse<UserResponse>> call = apiService.getMyInfo();// Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse> >call, @NonNull Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> userResponseApiResponse = response.body();
                    String endDateStr = userResponseApiResponse.getResult().getEndDate();

                    if (endDateStr != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                            Date endDate = sdf.parse(endDateStr);
                            Date now = new Date();
                            if (now.before(endDate)) {
                                isPrenium = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // handle parse error
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
