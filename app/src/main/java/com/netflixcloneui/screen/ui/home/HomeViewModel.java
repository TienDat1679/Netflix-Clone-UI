package com.netflixcloneui.screen.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.GenreRepository;
import com.netflixcloneui.data.MediaRepository;
import com.netflixcloneui.data.MovieRepository;
import com.netflixcloneui.data.TVSeriesRepository;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.Movie;
import com.netflixcloneui.model.TVSeries;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Genre>> genres = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, List<Media>>> homeMedia = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> moviesLiveDataByGenre = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> selectedGenreLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedMoviePosterLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSeriesSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isMoviesSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isGenresSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> cancelActionState = new MutableLiveData<>(false);
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final TVSeriesRepository seriesRepository;
    private final MediaRepository mediaRepository;

    public HomeViewModel(Context context) {
        genreRepository = new GenreRepository(context);
        movieRepository = new MovieRepository(context);
        seriesRepository = new TVSeriesRepository(context);
        mediaRepository = new MediaRepository(context);
        fetchGenres();
    }

    public void fetchGenres() {
        genreRepository.fetchGenres(genres, loadingLiveData);
        genres.observeForever(genres -> {
            if (genres != null) {
                mediaRepository.fetchMediaByGenres(genres, homeMedia, loadingLiveData);
            }
        });
    }

    public void fetchGenresForSeries() {
        genreRepository.fetchGenresForSeries(genres, loadingLiveData);
        genres.observeForever(genres -> {
            if (genres != null) {
                seriesRepository.fetchSeriesByGenres(genres, homeMedia, loadingLiveData);
            }
        });
    }

    public void fetchGenresForMovies() {
        genreRepository.fetchGenresForMovies(genres, loadingLiveData);
        genres.observeForever(genres -> {
            if (genres != null) {
                movieRepository.fetchMoviesByGenres(genres, homeMedia, loadingLiveData);
            }
        });
    }

    public void fetchMediaByGenre(Long genreId) {
        mediaRepository.fetchMediaByGenre(genreId, moviesLiveDataByGenre, loadingLiveData);

        moviesLiveDataByGenre.observeForever(movies -> {
            if (movies != null && !movies.isEmpty()) {
                int randomIndex = (int) (Math.random() * movies.size()); // Chọn ngẫu nhiên một phim
                selectedMoviePosterLiveData.setValue(movies.get(randomIndex).getPosterPath());
            }
        });
    }

    public LiveData<String> getSelectedMoviePoster() {
        return selectedMoviePosterLiveData;
    }

    public void setSelectedMoviePoster(String poster) {
        selectedMoviePosterLiveData.setValue(poster);
    }

    public LiveData<List<Genre>> getGenres() {
        return genres;
    }

    public LiveData<Map<Long, List<Media>>> getMedia() {
        return homeMedia;
    }

    public LiveData<List<Media>> getMoviesByGenre() {
        return moviesLiveDataByGenre;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getSelectedGenre() {
        return selectedGenreLiveData;
    }

    public void setSelectedGenre(String genreName) {
        selectedGenreLiveData.setValue(genreName);
    }

    // Getter
    public LiveData<Boolean> getIsSeriesSelected() { return isSeriesSelected; }
    public LiveData<Boolean> getIsMoviesSelected() { return isMoviesSelected; }
    public LiveData<Boolean> getIsGenresSelected() { return isGenresSelected; }
    public LiveData<Boolean> getCancelActionState() { return cancelActionState; }

    // Setter
    public void setSeriesSelected(boolean selected) {
        isSeriesSelected.setValue(selected);
        isMoviesSelected.setValue(false);
        isGenresSelected.setValue(false);
    }

    public void setMoviesSelected(boolean selected) {
        isMoviesSelected.setValue(selected);
        isSeriesSelected.setValue(false);
        isGenresSelected.setValue(false);
    }

    public void setGenresSelected(boolean selected) {
        isGenresSelected.setValue(selected);
        isMoviesSelected.setValue(false);
        isSeriesSelected.setValue(false);
    }

    public void setCancelActionState(boolean state) {
        cancelActionState.setValue(state);
    }
}