package com.netflixcloneui.screen.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.api.ApiService;
import com.netflixcloneui.api.RetrofitClient;
import com.netflixcloneui.data.GenreRepository;
import com.netflixcloneui.data.MovieRepository;
import com.netflixcloneui.model.Genre;
import com.netflixcloneui.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, List<Movie>>> moviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> moviesLiveDataByGenre = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> selectedGenreLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedMoviePosterLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSeriesSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isMoviesSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isGenresSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> cancelActionState = new MutableLiveData<>(false);
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public HomeViewModel(Context context) {
        genreRepository = new GenreRepository(context);
        movieRepository = new MovieRepository(context);
        fetchGenres();
    }

    public void fetchGenres() {
        loadingLiveData.setValue(true); // Bắt đầu loading
        genreRepository.fetchGenres(genresLiveData, loadingLiveData);
        genresLiveData.observeForever(genres -> {
            if (genres != null) {
                movieRepository.fetchMoviesByGenres(genres, moviesLiveData, loadingLiveData);
            }
        });
    }

    public void fetchGenresForSeries() {
        loadingLiveData.setValue(true); // Bắt đầu loading
        genreRepository.fetchGenresForSeries(genresLiveData, loadingLiveData);
        genresLiveData.observeForever(genres -> {
            if (genres != null) {
                movieRepository.fetchMoviesByGenres(genres, moviesLiveData, loadingLiveData);
            }
        });
    }

    public void fetchGenresForMovies() {
        loadingLiveData.setValue(true); // Bắt đầu loading
        genreRepository.fetchGenresForMovies(genresLiveData, loadingLiveData);
        genresLiveData.observeForever(genres -> {
            if (genres != null) {
                movieRepository.fetchMoviesByGenres(genres, moviesLiveData, loadingLiveData);
            }
        });
    }

    public void fetchMovieByGenre(Long genreId) {
        loadingLiveData.setValue(true);
        movieRepository.fetchMovieByGenre(genreId, moviesLiveDataByGenre, loadingLiveData);

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
        return genresLiveData;
    }

    public LiveData<Map<Long, List<Movie>>> getMovies() {
        return moviesLiveData;
    }

    public LiveData<List<Movie>> getMoviesByGenre() {
        return moviesLiveDataByGenre;
    }

//    public void setMoviesByGenre(List<Movie> movies) {
//        moviesLiveDataByGenre.setValue(movies);
//    }

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