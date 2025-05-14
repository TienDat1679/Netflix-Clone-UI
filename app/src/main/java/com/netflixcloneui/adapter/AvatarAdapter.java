package com.netflixcloneui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.netflixcloneui.R;
import com.netflixcloneui.model.response.UserResponse;
import com.netflixcloneui.ui.mynetflix.MyNetflixFragment;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {
    private List<String> avatars;
    private boolean isBase;
    private OnAvatarSelectedListener avatarSelectedListener;

    public void setOnAvatarSelectedListener(OnAvatarSelectedListener listener) {
        this.avatarSelectedListener = listener;
    }

    public AvatarAdapter(List<String> avatars, boolean isBase, OnAvatarSelectedListener listener) {
        this.avatars = avatars;
        this.isBase = isBase;
        this.avatarSelectedListener = listener;
    }

    @NonNull
    @Override
    public AvatarAdapter.AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarAdapter.AvatarViewHolder holder, int position) {
        String avatar = avatars.get(position);

        int resId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(avatar, "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.ivAvatar.setImageResource(resId);
        }

        if (isBase) {
            holder.cvBlock.setVisibility(View.GONE);
        }

        if (MyNetflixFragment.isPremium) {
            holder.cvBlock.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatar);
            }
        });

        if (holder.cvBlock.getVisibility() == View.VISIBLE) {
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return avatars != null ? avatars.size() : 0;
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        CardView cvBlock;
        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            cvBlock = itemView.findViewById(R.id.cvBlock);
        }
    }

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(String avatarName);
    }

}
