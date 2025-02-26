package com.netflixcloneui.screen.ui.coming_soon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComingSoonViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ComingSoonViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is coming soon fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}