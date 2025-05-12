package com.netflixcloneui.ui;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
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
import com.netflixcloneui.model.Media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

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
        recommendAdapter = new RecommendAdapter();
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this));
        rcvRecommend.setAdapter(recommendAdapter);

        // Load danh sách gợi ý ban đầu
        loadRecommend();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
