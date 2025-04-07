package com.netflixcloneui.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.netflixcloneui.data.repository.RepositoryCallback;
import com.netflixcloneui.data.repository.UserRepository;
import com.netflixcloneui.model.response.UserResponse;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLike = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isInWatchList = new MutableLiveData<>();

    public UserViewModel(Context context) {
        userRepository = new UserRepository(context);
    }

    public LiveData<String> getUserId() {
        if (userId.getValue() == null) {
            loadUserId();
        }
        return userId;
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

    public void checkMediaInWatchList(String userId, Long mediaId) {
        userRepository.checkMediaInWatchList(userId, mediaId, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isInWatchList.postValue(result);
            }

            @Override
            public void onError(String message) {
                Log.e("UserViewModel", message);
            }
        });
    }

    public void loadUserId() {
        userRepository.getMyInfo(new RepositoryCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse result) {
                userId.postValue(result.getId());
            }

            @Override
            public void onError(String message) {
                Log.e("UserViewModel", message);
            }
        });
    }
}
