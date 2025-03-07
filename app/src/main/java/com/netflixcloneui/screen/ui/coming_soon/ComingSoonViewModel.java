package com.netflixcloneui.screen.ui.coming_soon;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.MediaRepository;
import com.netflixcloneui.data.MovieRepository;
import com.netflixcloneui.data.TVSeriesRepository;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;

import java.util.List;

public class ComingSoonViewModel extends ViewModel {

    private final MutableLiveData<List<Media>> comingSoonMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> hotSeriesMovies = new MutableLiveData<>();
    private final MutableLiveData<List<TVSeries>> topSeries = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> topMovies = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MovieRepository movieRepository;
    private final TVSeriesRepository seriesRepository;
    private final MediaRepository mediaRepository;

    public ComingSoonViewModel(Context context) {
        movieRepository = new MovieRepository(context);
        seriesRepository = new TVSeriesRepository(context);
        mediaRepository = new MediaRepository(context);
        fetchComingSoonMovies();
        fetchHotSeriesMovies();
        fetchTop10Series();
        fetchTop10Movies();
    }

    private void fetchComingSoonMovies() {
        mediaRepository.fetchComingSoon(comingSoonMovies, isLoading);
    }

    private void fetchTop10Series() {
        seriesRepository.fetchTopTVSeries(topSeries, isLoading);
    }

    private void fetchTop10Movies() {
        movieRepository.fetchTopMovies(topMovies, isLoading);
    }

    private void fetchHotSeriesMovies() {
        mediaRepository.fetchHotSeriesMovies(hotSeriesMovies, isLoading);
    }

    public LiveData<List<Media>> getComingSoonMovies() {
        return comingSoonMovies;
    }

    public LiveData<List<Media>> getHotSeriesMovies() {
        return hotSeriesMovies;
    }

    public LiveData<List<TVSeries>> getTopSeries() {
        return topSeries;
    }

    public LiveData<List<Movie>> getTopMovies() {
        return topMovies;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
}