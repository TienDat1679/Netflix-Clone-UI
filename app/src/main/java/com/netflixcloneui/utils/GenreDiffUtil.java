package com.netflixcloneui.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenreDiffUtil extends DiffUtil.Callback {
    private final List<Genre> oldList;
    private final List<Genre> newList;
    private final Map<Long, List<Media>> oldMediaMap;
    private final Map<Long, List<Media>> newMediaMap;

    public GenreDiffUtil(List<Genre> oldList, List<Genre> newList,
                         Map<Long, List<Media>> oldMediaMap, Map<Long, List<Media>> newMediaMap) {
        this.oldList = oldList != null ? oldList : List.of(); // ✅ Tránh null
        this.newList = newList != null ? newList : List.of(); // ✅ Tránh null
        this.oldMediaMap = oldMediaMap != null ? oldMediaMap : Map.of(); // ✅ Tránh null
        this.newMediaMap = newMediaMap != null ? newMediaMap : Map.of(); // ✅ Tránh null
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
        Genre oldGenre = oldList.get(oldItemPosition);
        Genre newGenre = newList.get(newItemPosition);

        // ✅ Kiểm tra xem nội dung thể loại có thay đổi không
        boolean isSameGenre = oldGenre.equals(newGenre);

        // ✅ Kiểm tra danh sách phim của thể loại đó
        List<Media> oldMedia = oldMediaMap.getOrDefault(oldGenre.getId(), List.of());
        List<Media> newMedia = newMediaMap.getOrDefault(newGenre.getId(), List.of());

        boolean isSameMedia = Objects.equals(oldMedia, newMedia);

        return isSameGenre && isSameMedia;
    }
}


