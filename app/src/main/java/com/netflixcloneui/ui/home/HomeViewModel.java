package com.netflixcloneui.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.repository.GenreRepository;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.data.repository.MovieRepository;
import com.netflixcloneui.data.repository.TVSeriesRepository;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Media;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Genre>> genres = new MutableLiveData<>();
    private final MutableLiveData<Media> panelMedia = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, List<Media>>> homeMedia = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> top10Movies = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> top10Series = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> moviesLiveDataByGenre = new MutableLiveData<>();
    private final MutableLiveData<String> selectedGenreLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedMoviePosterLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
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
        fetchTop10Series();
        fetchTop10Movies();
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
                panelMedia.setValue(movies.get(randomIndex));
            }
        });
    }

    public void fetchTop10Series() {
        mediaRepository.fetchTopTVSeries(top10Series, loadingLiveData);
        top10Series.observeForever(series -> {
            if (series == null || series.isEmpty()) {
                Log.e("HomeViewModel", "fetchTop10Series: Không có dữ liệu series!");
            } else {
                Log.d("HomeViewModel", "fetchTop10Series: Đã load " + series.size() + " series");
                panelMedia.setValue(top10Series.getValue().get(0));
            }
        });
    }

    private void fetchTop10Movies() {
        mediaRepository.fetchTopMovies(top10Movies, loadingLiveData);
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

    public void setMedia(Map<Long, List<Media>> media) {
        homeMedia.setValue(media);
    }

    public LiveData<List<Media>> get10Series() {
        return top10Series;
    }

    public LiveData<List<Media>> get10Movies() {
        return top10Movies;
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

    public void setPanelMedia(Media media) {
        panelMedia.setValue(media);
    }

    public LiveData<Media> getPanelMedia() {
        return panelMedia;
    }

    public void setCancelActionState(boolean state) {
        cancelActionState.setValue(state);
    }
}