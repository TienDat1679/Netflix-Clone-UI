package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.RecommendAdapter;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Media;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText edtSearch;
    private ImageView imgBack;
    private RecyclerView rcvRecommend;
    private TextView noResult;
    private TextView text;
    private MediaRepository mediaRepository;
    private RecommendAdapter recommendAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);;

        edtSearch = (EditText) findViewById(R.id.edt_search);
        rcvRecommend = (RecyclerView) findViewById(R.id.rcv_recommend);
        imgBack = (ImageView) findViewById(R.id.img_back);
        text = (TextView) findViewById(R.id.text);
        noResult = (TextView) findViewById(R.id.tv_sorry);

        mediaRepository = new MediaRepository(this);
        recommendAdapter = new RecommendAdapter();
        rcvRecommend.setLayoutManager(new LinearLayoutManager(this));
        rcvRecommend.setAdapter(recommendAdapter);

        loadRecommend();

        imgBack.setOnClickListener(v -> finish());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    text.setVisibility(View.GONE);
                    searchMovies(s.toString());  // Gọi API tìm kiếm nếu nhập >= 3 ký tự
                } else if (s.length() == 0) {
                    text.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.GONE);
                    rcvRecommend.setVisibility(View.VISIBLE); // Hiển thị RecyclerView
                    loadRecommend();  // Nếu rỗng, hiển thị danh sách Recommended
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchMovies(String keyword) {
        mediaRepository.searchMedia(keyword, new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    recommendAdapter.setMedia(response.body()); // Cập nhật RecyclerView
                    noResult.setVisibility(View.GONE); // Ẩn TextView thông báo
                    rcvRecommend.setVisibility(View.VISIBLE); // Hiển thị RecyclerView
                } else {
                    noResult.setVisibility(View.VISIBLE); // Hiển thị TextView thông báo
                    rcvRecommend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi API: " + t.getMessage());
                noResult.setVisibility(View.VISIBLE); // Hiển thị thông báo lỗi
                rcvRecommend.setVisibility(View.GONE); // Ẩn danh sách
            }
        });
    }

    private void loadRecommend() {
        mediaRepository.getTrendingMedia(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendAdapter.setMedia(response.body()); // Cập nhật dữ liệu
                } else {
                    Log.e("API_ERROR", "Không có dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi API: " + t.getMessage());
            }
        });
    }
}