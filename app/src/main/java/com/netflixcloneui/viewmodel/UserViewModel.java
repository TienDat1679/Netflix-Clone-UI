package com.netflixcloneui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.repository.MediaRepository;
import com.netflixcloneui.data.repository.RepositoryCallback;
import com.netflixcloneui.data.repository.UserRepository;
import com.netflixcloneui.model.Media;
import com.netflixcloneui.model.response.UserResponse;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<UserResponse> user = new MutableLiveData<>();
    private final MutableLiveData<List<Media>> watchList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isInWatchList = new MutableLiveData<>(false);
    private final MediaRepository mediaRepository;

    public UserViewModel(Context context) {
        userRepository = new UserRepository(context);
        mediaRepository = new MediaRepository(context);
    }

    public LiveData<String> getUserId() {
        if (userId.getValue() == null) {
            loadUserId();
        }
        return userId;
    }

    public LiveData<UserResponse> getUser() {
        if (user.getValue() == null) {
            loadUserId();
        }
        return user;
    }

    public void setUser(UserResponse user) {
        this.user.setValue(user);
    }

    public LiveData<Boolean> getIsLike() {
        if (isLike.getValue() == null) {
            isLike.postValue(false);
        }
        return isLike;
    }

    public void setIsLike(Boolean isLike) {
        this.isLike.postValue(isLike);
    }

    public LiveData<Boolean> getIsInWatchList() {
        return isInWatchList;
    }

    public void setIsInWatchList (Boolean isInWatchList) {
        this.isInWatchList.postValue(isInWatchList);
    }

    public LiveData<List<Media>> getWatchList() {
        return watchList;
    }

    public void checkMediaInLikeList(String userId, Long mediaId) {
        userRepository.checkMediaInLikeList(userId, mediaId, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isLike.postValue(result);
            }

            @Override
            public void onError(String message) {
                Log.e("UserViewModel", message);
            }
        });
    }

    public void checkMediaInWatchList(String userId, Long mediaId, RepositoryCallback<Boolean> callback) {
        userRepository.checkMediaInWatchList(userId, mediaId, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isInWatchList.postValue(result);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("UserViewModel", message);
                if (callback != null) {
                    callback.onError(message);
                }
            }
        });
    }

    public void loadUserId() {
        userRepository.getMyInfo(new RepositoryCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse result) {
                userId.postValue(result.getId());
                user.postValue(result);
            }

            @Override
            public void onError(String message) {
                userId.postValue(null);
                Log.e("UserViewModel", message);
            }
        });
    }

    public void fetchUserWatchList(String userId) {
        mediaRepository.getWatchLists(userId, new RepositoryCallback<List<Media>>() {
            @Override
            public void onSuccess(List<Media> data) {
                watchList.postValue(data);
                Log.d("UserViewModel", "User watch list fetched successfully");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("UserViewModel", "Error fetching user watch list: " + errorMessage);
            }
        });
    }
}
