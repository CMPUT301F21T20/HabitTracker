package com.example.habittracker.ui.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.models.Habit.Habit;

import java.util.List;

public class FollowViewModel extends ViewModel {

    private MutableLiveData<List<Habit>> userHabits;

    public FollowViewModel() {
        userHabits = new MutableLiveData<>();
    }


    public LiveData<List<Habit>> getList() {
        return userHabits;
    }
}
