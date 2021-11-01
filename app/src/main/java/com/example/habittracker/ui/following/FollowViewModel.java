package com.example.habittracker.ui.following;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FollowViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public FollowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is check user fragment");
    }


    public LiveData<String> getText() {
        return mText;
    }
}
