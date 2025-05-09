package com.netflixcloneui.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.R;
import com.netflixcloneui.model.response.CommentResponse;
import com.netflixcloneui.utils.TimeAgoUtil;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentResponse> comments;
    private OnLikeClickListener likeClickListener;

    public CommentAdapter(List<CommentResponse> comments) {
        this.comments = comments;
    }

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.likeClickListener = listener;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        CommentResponse comment = comments.get(position);
        holder.userName.setText(comment.getUserName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.createdAt.setText(TimeAgoUtil.getTimeAgo(comment.getCreatedAt()));
        }
        holder.content.setText(comment.getContent());
        holder.likes.setText(String.valueOf(comment.getLikes()));
        if (comment.isLikedByUser()) {
            holder.iconLike.setImageResource(R.drawable.ic_like_heart_shape);
        } else {
            holder.iconLike.setImageResource(R.drawable.ic_unlike_heart_shape);
        }
        holder.iconLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onLikeClick(position, comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, iconLike;
        TextView userName, createdAt, content, likes;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ivAvatar);
            iconLike = itemView.findViewById(R.id.ivLikeIcon);
            userName = itemView.findViewById(R.id.tvUserName);
            createdAt = itemView.findViewById(R.id.tvCreatedAt);
            content = itemView.findViewById(R.id.tvContent);
            likes = itemView.findViewById(R.id.tvLikes);
        }
    }

    public interface OnLikeClickListener {
        void onLikeClick(int position, CommentResponse comment);
    }

}
