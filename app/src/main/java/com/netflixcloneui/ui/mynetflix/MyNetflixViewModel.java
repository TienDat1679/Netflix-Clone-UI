package com.netflixcloneui.ui.mynetflix;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.remote.ApiService;
import com.netflixcloneui.data.remote.RetrofitClient;
import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.data.repository.RepositoryCallback;
import com.netflixcloneui.model.Media;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNetflixViewModel extends ViewModel {
    private final MutableLiveData<List<Media>> likeLists = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> watchLists = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final MediaRepository mediaRepository;

    public MyNetflixViewModel(Context context) {
        mediaRepository = new MediaRepository(context);
    }

    public LiveData<List<Media>> getFavoriteMovies() {
        return likeLists;
    }

    public LiveData<List<Media>> getUserMovieList() {
        return watchLists;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void fetchUserLikeList(String userId) {
        mediaRepository.getLikeLists(userId, new RepositoryCallback<List<Media>>() {
            @Override
            public void onSuccess(List<Media> data) {
                likeLists.postValue(data);
            }

            @Override
            public void onError(String errorMessage) {
                likeLists.postValue(null);
                Log.e("MyNetflixViewModel", "Error fetching user like list: " + errorMessage);
            }
        });
    }

    public void fetchUserWatchList(String userId) {
        mediaRepository.getWatchLists(userId, new RepositoryCallback<List<Media>>() {
            @Override
            public void onSuccess(List<Media> data) {
                watchLists.postValue(data);
            }

            @Override
            public void onError(String errorMessage) {
                watchLists.postValue(null);
                Log.e("MyNetflixViewModel", "Error fetching user watch list: " + errorMessage);
            }
        });
    }
}