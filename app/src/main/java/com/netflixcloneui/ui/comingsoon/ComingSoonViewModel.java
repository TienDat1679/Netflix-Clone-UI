package com.netflixcloneui.ui.comingsoon;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.model.Media;

import java.util.List;

public class ComingSoonViewModel extends ViewModel {

    private final MutableLiveData<List<Media>> comingSoonMedia = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> hotMedia = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> topSeries = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> topMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MediaRepository mediaRepository;

    public ComingSoonViewModel(Context context) {
        mediaRepository = new MediaRepository(context);
        fetchComingSoonMedia();
        fetchHotMedia();
        fetchTop10Series();
        fetchTop10Movies();
    }

    private void fetchComingSoonMedia() {
        mediaRepository.fetchComingSoon(comingSoonMedia, isLoading);
    }

    private void fetchTop10Series() {
        mediaRepository.fetchTopTVSeries(topSeries, isLoading);
    }

    private void fetchTop10Movies() {
        mediaRepository.fetchTopMovies(topMovies, isLoading);
    }

    private void fetchHotMedia() {
        mediaRepository.fetchHotSeriesMovies(hotMedia, isLoading);
    }

    public LiveData<List<Media>> getComingSoonMedia() {
        return comingSoonMedia;
    }

    public LiveData<List<Media>> getHotMedia() {
        return hotMedia;
    }

    public LiveData<List<Media>> getTopSeries() {
        return topSeries;
    }

    public LiveData<List<Media>> getTopMovies() {
        return topMovies;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
}