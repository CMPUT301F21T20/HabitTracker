package com.example.habittracker.ui.follower;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class FollowerViewModel extends ViewModel {

    private MutableLiveData<List> followerList;

    public FollowerViewModel() {
        followerList = new MutableLiveData<>();
    }


    public LiveData<List> getList() {
        return followerList;
    }
}
