package com.netflixcloneui.ui;

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

import java.util.ArrayList;
import java.util.Collections;
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

    private static final String ARG_MEDIA_ID = "media_id";
    private static final String ARG_SEASON = "season";

    private RecyclerView recyclerViewEps;
    private EpisodeAdapter epsAdapter;
    private SnapHelper snapHelper;

    private long mediaId;
    private int season;
    private List<Episode> listEps = new ArrayList<>();

    public EpisodeFragment() {
        // Required empty public constructor
    }

    public static EpisodeFragment newInstance(long mediaId, int season) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MEDIA_ID, mediaId);
        args.putInt(ARG_SEASON, season);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getLong(ARG_MEDIA_ID, -1);
            season = getArguments().getInt(ARG_SEASON, 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_episode, container, false);

        // Khởi tạo RecyclerView và SnapHelper chỉ 1 lần
        recyclerViewEps = view.findViewById(R.id.recyclerEpisodes);
        recyclerViewEps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // SnapHelper chỉ attach 1 lần
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewEps);

        // Lấy dữ liệu episodes khi view được tạo
        getEsp(mediaId, season, view);
        return view;
    }

    private void getEsp(long id, int season, View view) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id);

        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>> call, @NonNull Response<List<Episode>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("getEsp", "API error or empty");
                    return;
                }

                // Lấy toàn bộ episodes và lọc theo season
                List<Episode> allEpisodes = response.body();
                List<Episode> filteredEpisodes = new ArrayList<>();
                for (Episode episode : allEpisodes) {
                    if (episode.getSeasonNumber() == season) {
                        filteredEpisodes.add(episode);
                    }
                }

                // Sắp xếp theo episodeNumber tăng dần
                Collections.sort(filteredEpisodes, (e1, e2) -> Integer.compare(e1.getEpisodeNumber(), e2.getEpisodeNumber()));

                // Cập nhật adapter với dữ liệu đã lọc và sắp xếp
                if (epsAdapter == null) {
                    epsAdapter = new EpisodeAdapter(getContext(), filteredEpisodes, new EpisodeAdapter.OnEpisodeClickListener() {
                        @Override
                        public void onEpisodeClick(Long episodeId) {
                            Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                            intent.putExtra("VIDEO_ID", episodeId);
                            startActivity(intent);
                        }
                    });
                    recyclerViewEps.setAdapter(epsAdapter);
                } else {
                    epsAdapter.setData(filteredEpisodes);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("getEsp", "API Call failed: " + t.getMessage());
            }
        });
    }

    public void reload(int newSeason) {
        this.season = newSeason;
        if (getView() != null) {
            getEsp(mediaId, newSeason, getView());
        }
    }
}
