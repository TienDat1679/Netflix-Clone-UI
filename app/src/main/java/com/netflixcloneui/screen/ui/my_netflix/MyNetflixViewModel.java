package com.netflixcloneui.screen.ui.my_netflix;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyNetflixViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyNetflixViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my netflix fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}