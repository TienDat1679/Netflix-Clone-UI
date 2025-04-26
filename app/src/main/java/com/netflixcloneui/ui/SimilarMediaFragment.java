package com.netflixcloneui.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Media;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SimilarMediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimilarMediaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEDIA_ID = "media_id";

    // TODO: Rename and change types of parameters
    private long mediaId;
    private RecyclerView recyclerView;
    List<Media> listMedia;

    public SimilarMediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mediaId Parameter 1.
     * @return A new instance of fragment SimilarMediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SimilarMediaFragment newInstance(long mediaId) {
        SimilarMediaFragment fragment = new SimilarMediaFragment();
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
        View view = inflater.inflate(R.layout.fragment_similar_media, container, false);
        getMediaSame(mediaId, view);
        return view;
    }

    private void getMediaSame(long id, View view)
    {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<List<Media>> call = apiService.getSameMedia(id); // Không cần chuyển đổi bằng `Long.valueOf()`
        call.enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(@NonNull Call<List<Media> >call, @NonNull Response<List<Media>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listMedia = response.body();
                    recyclerView = view.findViewById(R.id.rcvSimilarMedia);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3); // 3 cột
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(new MediaAdapter(listMedia, MediaAdapter.TYPE_NORMAL));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Media>> call, @NonNull Throwable t) {
                Log.e("MovieDetail", "API Call failed: " + t.getMessage());
            }
        });
    }
}