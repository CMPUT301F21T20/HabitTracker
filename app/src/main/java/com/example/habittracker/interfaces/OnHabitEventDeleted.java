package com.example.habittracker.interfaces;

/**
 * Interface to handle asynchronous nature
 * of deleting deleteing a habit event
 */
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
