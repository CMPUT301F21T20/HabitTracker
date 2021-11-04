package com.example.habittracker.ui.following;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.classes.Habit;

import java.util.List;

public class FollowingViewModel extends ViewModel {

    private MutableLiveData<List> followingList;

    public FollowingViewModel() {
        followingList = new MutableLiveData<>();
    }


    public LiveData<List> getList() {
        return followingList;
    }
}
