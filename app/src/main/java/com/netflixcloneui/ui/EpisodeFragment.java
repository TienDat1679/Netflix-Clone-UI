package com.netflixcloneui.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.EpisodeAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Episode;
import com.netflixcloneui.model.request.PlaybackProgressRequest;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.PlayBackResponse;
import com.netflixcloneui.model.response.UserResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EpisodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpisodeFragment extends Fragment {
    private RecyclerView recyclerViewEps;
    private EpisodeAdapter    epsAdapter;
    private SnapHelper snapHelper;

    private List<PlayBackResponse> playbackProgress;
    private long mediaId;
    private int season;

    private boolean isPrenium=false;
    private boolean isPre=false;
    private List<Episode> allEpisodes = new ArrayList<>();   // giữ toàn bộ data

    public static EpisodeFragment newInstance(long mediaId, int season) {
        EpisodeFragment f = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putLong("media_id", mediaId);
        args.putInt("season", season);
        f.setArguments(args);
        return f;
    }

    @Override public void onCreate(Bundle s) {
        super.onCreate(s);
        mediaId = getArguments().getLong("media_id");
        season  = getArguments().getInt("season");
    }
    private void checkPrenium() {
        ApiService apiService = RetrofitClient.getApiService(getContext());
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

    private void showContinueWatchingDialog(Long savedPosition,Long mediaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tiếp tục xem?");
        builder.setMessage("Bạn muốn tiếp tục xem từ phút " + (savedPosition / 60000) + " không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",savedPosition);// Truyền videoId vào Intent
            startActivity(intent);
        });

        builder.setNegativeButton("Xem lại từ đầu", (dialog, which) -> {

            ApiService apiService = RetrofitClient.getApiService(getContext());
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
            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
            intent.putExtra("VIDEO_ID", mediaId);
            intent.putExtra("position",0);// Truyền videoId vào Intent
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showPreniumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Phim chỉ dành cho tài khoan prenium");
        builder.setMessage("Hãy trỏe thành thành vien prenium");

        builder.setPositiveButton("Đăng ki prenium", (dialog, which) -> {
            Intent intent = new Intent(getContext(), PaymentPackageActivity.class);
            startActivity(intent);
        });

        builder.setNegativeButton("Đóng", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override public View onCreateView(LayoutInflater i, ViewGroup c, Bundle s) {
        View view = i.inflate(R.layout.fragment_episode, c, false);

        recyclerViewEps = view.findViewById(R.id.recyclerEpisodes);
        recyclerViewEps.setLayoutManager(new LinearLayoutManager(getContext()));
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewEps);

        epsAdapter = new EpisodeAdapter(getContext(), new ArrayList<>(), id -> {
            if (getContext().getSharedPreferences("MyAppPrefs",Context.MODE_PRIVATE).getString("jwt_token", null) != null) {
                if(isPre)
                {
                    if(isPrenium)
                    {
                        ApiService apiService = RetrofitClient.getApiService(getContext());
                        Call<PlayBackResponse> call = apiService.getPlaybackProgress(id); // Không cần chuyển đổi bằng `Long.valueOf()`
                        call.enqueue(new Callback<PlayBackResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    PlayBackResponse PlayBackResponse = response.body();
                                    showContinueWatchingDialog(PlayBackResponse.getPosition(),id);
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                                Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                                intent.putExtra("VIDEO_ID", id);
                                intent.putExtra("postion",0);// Truyền videoId vào Intent
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        showPreniumDialog();
                    }
                }
                else {
                    ApiService apiService = RetrofitClient.getApiService(getContext());
                    Call<PlayBackResponse> call = apiService.getPlaybackProgress(id); // Không cần chuyển đổi bằng `Long.valueOf()`
                    call.enqueue(new Callback<PlayBackResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PlayBackResponse >call, @NonNull Response<PlayBackResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                PlayBackResponse PlayBackResponse = response.body();
                                showContinueWatchingDialog(PlayBackResponse.getPosition(),id);
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<PlayBackResponse> call, @NonNull Throwable t) {
                            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                            intent.putExtra("VIDEO_ID", id);
                            intent.putExtra("postion",0);// Truyền videoId vào Intent
                            startActivity(intent);
                        }
                    });
                }

            } else {
                TvSeriesDetailActivity.showLoginDialog(getContext());
            }
        });
        recyclerViewEps.setAdapter(epsAdapter);

        // Lần đầu gọi API để load tất cả episodes
        loadAllEpisodes();
        getPlayBackProgress();
        return view;
    }


    private void getPlayBackProgress(){
        RetrofitClient.getApiService(getContext())
                .getPlaybackProgressByUser()
                .enqueue(new Callback<List<PlayBackResponse>>() {
                    @Override public void onResponse(@NonNull Call<List<PlayBackResponse>> c, @NonNull Response<List<PlayBackResponse>> r) {
                        if (!r.isSuccessful() || r.body()==null) return;
                        Log.d("api", r.body().toString());
                        playbackProgress = r.body();
                        Log.d("playbackk", playbackProgress.toString());

                    }
                    @Override public void onFailure(@NonNull Call<List<PlayBackResponse>> c, @NonNull Throwable t) { }
                });
    }
    private void loadAllEpisodes() {
        RetrofitClient.getApiService(getContext())
                .getEspOfSeries(mediaId)
                .enqueue(new Callback<List<Episode>>() {
                    @Override public void onResponse(Call<List<Episode>> c, Response<List<Episode>> r) {
                        if (!r.isSuccessful() || r.body()==null) return;
                        allEpisodes = r.body();               // lưu toàn bộ
                        applySeasonFilter();                  // lần đầu filter theo season khởi tạo
                    }
                    @Override public void onFailure(Call<List<Episode>> c, Throwable t) { }
                });
    }

    // Lọc và sắp xếp theo season hiện tại, cập nhật adapter
    private void applySeasonFilter() {
        List<Episode> filtered = new ArrayList<>();
        for (Episode e : allEpisodes) {
            if (e.getSeasonNumber() == season) filtered.add(e);
        }
        Collections.sort(filtered, (a,b) -> Integer.compare(a.getEpisodeNumber(), b.getEpisodeNumber()));
        epsAdapter.setData(filtered,playbackProgress);
    }

    // Khi người dùng chọn season mới
    public void reload(int newSeason) {
        this.season = newSeason;
        applySeasonFilter();   // chỉ lọc lại, không gọi API
    }
}
