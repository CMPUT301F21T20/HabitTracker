package com.example.habittracker.interfaces;

import com.example.habittracker.classes.HabitList;

// This class wil be used to handle the asynchronous firestore call
// to retrieve the habit list
public interface OnHabitListRetrieved {
    /**
     * Funciton to be run when habit list is retrieved
     * @param habitList the habitList
     */
    void onHabitListRetrieved(HabitList habitList);

    /**
     * If there's an error in retrieving the habitlist
     * @param taskException the exception to raise
     */
    void onError(Exception taskException);
}
