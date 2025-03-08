package com.netflixcloneui.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.netflixcloneui.model.Media;

import java.util.List;
import java.util.Objects;

public class MediaDiffUtil extends DiffUtil.Callback {
    private final List<Media> oldList;
    private final List<Media> newList;

    public MediaDiffUtil(List<Media> oldList, List<Media> newList) {
        this.oldList = oldList != null ? oldList : List.of(); // ✅ Tránh null
        this.newList = newList != null ? newList : List.of(); // ✅ Tránh null
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Media oldMedia = oldList.get(oldItemPosition);
        Media newMedia = newList.get(newItemPosition);

        return oldMedia.equals(newMedia);
    }
}
