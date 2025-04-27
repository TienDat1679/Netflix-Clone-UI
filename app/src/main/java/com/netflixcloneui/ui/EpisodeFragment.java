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
    private RecyclerView recyclerViewEps;
    private EpisodeAdapter epsAdapter;
    private SnapHelper snapHelper;

    private long mediaId;
    private int season;
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

    @Override public View onCreateView(LayoutInflater i, ViewGroup c, Bundle s) {
        View view = i.inflate(R.layout.fragment_episode, c, false);

        recyclerViewEps = view.findViewById(R.id.recyclerEpisodes);
        recyclerViewEps.setLayoutManager(new LinearLayoutManager(getContext()));
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewEps);

        // tạo adapter với list rỗng
        epsAdapter = new EpisodeAdapter(getContext(), new ArrayList<>(), id -> {
            Intent it = new Intent(getContext(), FullScreenVideoActivity.class);
            it.putExtra("VIDEO_ID", id);
            startActivity(it);
        });
        recyclerViewEps.setAdapter(epsAdapter);

        // Lần đầu gọi API để load tất cả episodes
        loadAllEpisodes();

        return view;
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
        epsAdapter.setData(filtered);
    }

    // Khi người dùng chọn season mới
    public void reload(int newSeason) {
        this.season = newSeason;
        applySeasonFilter();   // chỉ lọc lại, không gọi API
    }
}
