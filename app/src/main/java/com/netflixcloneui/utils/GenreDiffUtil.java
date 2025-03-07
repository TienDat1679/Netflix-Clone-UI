package com.netflixcloneui.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.netflixcloneui.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreDiffUtil extends DiffUtil.Callback {
    private final List<Genre> oldList;
    private final List<Genre> newList;

    public GenreDiffUtil(List<Genre> oldList, List<Genre> newList) {
        this.oldList = oldList != null ? oldList : new ArrayList<>(); // ✅ Tránh null
        this.newList = newList != null ? newList : new ArrayList<>(); // ✅ Tránh null
    }

    @Override
    public int getOldListSize() { return oldList.size(); }

    @Override
    public int getNewListSize() { return newList.size(); }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

