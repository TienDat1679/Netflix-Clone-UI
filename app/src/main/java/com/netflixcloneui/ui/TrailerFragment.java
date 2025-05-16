package com.netflixcloneui.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.TrailerAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Trailer;
import com.netflixcloneui.model.response.ApiResponse;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrailerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDIA_ID = "media_id";
    private static final String ARG_TRAILER_LIST = "trailer_list";

    // TODO: Rename and change types of parameters
    private long mediaId;
    private List<Trailer> trailers;
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;

    public TrailerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mediaId Parameter 1.
     * @param trailers Parameter 2.
     * @return A new instance of fragment TrailerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrailerFragment newInstance(long mediaId, List<Trailer> trailers) {
        TrailerFragment fragment = new TrailerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MEDIA_ID, mediaId);
        args.putSerializable(ARG_TRAILER_LIST, (Serializable) trailers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getLong(ARG_MEDIA_ID, -1);
            trailers = (ArrayList<Trailer>) getArguments().getSerializable("trailer_list");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailer, container, false);
        getTrailers(view);
        return view;
    }

    private void setTrailers(View view) {
        recyclerView = view.findViewById(R.id.rcvTrailers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Đăng ký vòng đời của Activity cho Adapter
        trailerAdapter = new TrailerAdapter(trailers, (LifecycleOwner) getContext());
        recyclerView.setAdapter(trailerAdapter);
    }

    private void getTrailers(View view) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<ApiResponse<List<Trailer>>> call = apiService.getMediaTrailers(mediaId); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<ApiResponse<List<Trailer>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Trailer>>>call, @NonNull Response<ApiResponse<List<Trailer>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    trailers = response.body().getResult();
                    recyclerView = view.findViewById(R.id.rcvTrailers);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    // Đăng ký vòng đời của Activity cho Adapter
                    trailerAdapter = new TrailerAdapter(trailers, (LifecycleOwner) getContext());
                    recyclerView.setAdapter(trailerAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Trailer>>> call, @NonNull Throwable t) {
                Log.e("Trailer", "API Call failed: " + t.getMessage());
            }
        });
    }
}