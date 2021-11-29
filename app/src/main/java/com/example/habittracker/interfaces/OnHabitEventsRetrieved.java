package com.example.habittracker.interfaces;

import com.example.habittracker.models.HabitEvent.HabitEventList;

public interface OnHabitEventsRetrieved {
    /**
     * Funciton to be run when habit events are retrieved
     * @param habitEventList the habitList
     */
    void onHabitEventsRetrieved(HabitEventList habitEventList);

    /**
     * If there's an error in retrieving the habit events
     * @param taskException the exception to raise
     */
    void onError(Exception taskException);
}
