package com.example.habittracker.interfaces;

public interface OnHabitEventDeleted {
    /**
     * Funciton to be run when habit events is deleted
     */
    void onHabitEventDeleted();

    /**
     * If there's an error in retrieving the habit events
     * @param taskException the exception to raise
     */
    void onError(Exception taskException);
}
