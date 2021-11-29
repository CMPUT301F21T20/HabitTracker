package com.example.habittracker.interfaces;

import com.example.habittracker.models.HabitEventList;

/**
 * Interface to handle asynchronous nature
 * of retrieving habit events
 */
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
