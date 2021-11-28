package com.example.habittracker.ui.follower;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.models.User;

import java.util.List;

public class FollowerViewModel extends ViewModel {

    private MutableLiveData<List<User>> followerList;

    public FollowerViewModel() {
        followerList = new MutableLiveData<>();
    }


    public LiveData<List<User>> getList() {
        return followerList;
    }
}
