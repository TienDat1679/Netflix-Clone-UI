package com.netflixcloneui.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netflixcloneui.R;
import com.netflixcloneui.adapter.CommentAdapter;
import com.netflixcloneui.adapter.MediaAdapter;
import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.response.ApiResponse;
import com.netflixcloneui.model.response.CommentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {
    private long mediaId;
    private CommentAdapter commentAdapter;
    private List<CommentResponse> comments;
    private RecyclerView recyclerView;

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(long mediaId) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putLong("media_id", mediaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getLong("media_id", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        setComments(view);
        return view;
    }

    private void setComments(View view) {
        ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<ApiResponse<List<CommentResponse>>> call = apiService.getCommentsByMediaId(mediaId, 0, 10);
        call.enqueue(new Callback<ApiResponse<List<CommentResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CommentResponse>>> call, @NonNull Response<ApiResponse<List<CommentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comments = response.body().getResult();
                    recyclerView = view.findViewById(R.id.rcvComments);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    commentAdapter = new CommentAdapter(comments);
                    commentAdapter.setOnLikeClickListener((position, comment) -> {
                        if (comment.isLikedByUser()) {
                            // Unlike
                            comment.setLikedByUser(false);
                            comment.setLikes(comment.getLikes() - 1);
                            commentAdapter.notifyItemChanged(position);

                            RetrofitClient.getApiService(getContext()).unlikeComment(comment.getId())
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                            // Xử lý nếu cần
                                            if (!response.isSuccessful()) {
                                                Log.e("CommentFragment", "Unlike API failed: " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Log.e("CommentFragment", "Unlike failed: " + t.getMessage());
                                        }
                                    });

                        } else {
                            // Like
                            comment.setLikedByUser(true);
                            comment.setLikes(comment.getLikes() + 1);
                            commentAdapter.notifyItemChanged(position);

                            RetrofitClient.getApiService(getContext()).likeComment(comment.getId())
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                            if (!response.isSuccessful()) {
                                                Log.e("CommentFragment", "Like API failed: " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Log.e("CommentFragment", "Like failed: " + t.getMessage());
                                        }
                                    });
                        }

                        // Cập nhật item sau khi thay đổi
                        commentAdapter.notifyItemChanged(position);
                    });
                    recyclerView.setAdapter(commentAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CommentResponse>>> call, @NonNull Throwable t) {
                Log.e("CommentFragment", "API Call failed: " + t.getMessage());
            }
        });
    }

    public void addNewComment(CommentResponse comment) {
        if (comments != null && commentAdapter != null) {
            comments.add(0, comment); // thêm comment mới lên đầu danh sách
            commentAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        }
    }
}