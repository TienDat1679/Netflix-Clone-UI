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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDIA_ID = "media_id";
    private RecyclerView recyclerViewEps;
    private EpisodeAdapter EpsAdapter;
    private YouTubePlayer youTubePlayerInstance;
    private List<Episode> listEps;

    // TODO: Rename and change types of parameters
    private long mediaId;

    public EpisodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mediaId Parameter 1.
     * @return A new instance of fragment EpisodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EpisodeFragment newInstance(long mediaId) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MEDIA_ID, mediaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getLong(ARG_MEDIA_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode, container, false);
        getEsp(mediaId, view);
        return view;
    }

    private void getEsp(long id, View view) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<List<Episode>> call = apiService.getEspOfSeries(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>>call, @NonNull Response<List<Episode>> response) {
                listEps = response.body();
                recyclerViewEps = view.findViewById(R.id.recyclerEpisodes);

                // Thiết lập RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerViewEps.setLayoutManager(layoutManager);
                EpsAdapter = new EpisodeAdapter(getContext(), listEps, new EpisodeAdapter.OnEpisodeClickListener() {
                    @Override
                    public void onEpisodeClick(String videoKey) {
                        //youTubePlayerInstance.loadVideo(videoKey, 0);
                        Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                        intent.putExtra("VIDEO_ID", "xbsT5l4hdfA"); // Truyền videoId vào Intent
                        startActivity(intent);
                    }
                });
                recyclerViewEps.setAdapter(EpsAdapter);

                // Dùng SnapHelper để cuộn từng phim một cách mượt mà
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerViewEps);
            }
            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.e("Tvseries Eps", "API Call failed: " + t.getMessage());
            }
        });
    }
}