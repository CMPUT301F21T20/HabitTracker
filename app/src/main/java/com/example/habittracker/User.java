package com.example.habittracker;


public class User {
    private final String uid;
    private HabitList habitList;

    public User(String uid) {
        this.uid = uid;
        this.habitList = new HabitList();

        // get current habit list once created in firebase
    }

    public Habit createHabit(String title, String reason, Number frequency) {
        Habit newHabit = Habit(title, reason, frequency);
        this.habitList.addHabit(newHabit);
        return newHabit;
    }

    public Boolean deleteHabit(String habitId) {}

    public String getUid() {
        return uid;
    }

    public HabitList getHabitList() {
        return this.habitList;
    }
}
