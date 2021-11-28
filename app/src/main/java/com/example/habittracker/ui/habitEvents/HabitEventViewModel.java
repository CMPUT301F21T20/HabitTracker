package com.example.habittracker.ui.habitEvents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.models.Habit;

import java.util.List;

public class HabitEventViewModel extends ViewModel {
    private MutableLiveData<List<Habit>> myHabits;

    public HabitEventViewModel() {
        myHabits = new MutableLiveData<>();
    }


    public LiveData<List<Habit>> getList() {
        return myHabits;
    }
}