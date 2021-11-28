package com.example.habittracker.classes;

import java.util.ArrayList;

public class UsersList {

    private ArrayList<User> usersList;

    public UsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    public UsersList() {
        this.usersList = new ArrayList<>();
    }

    /**
     * this function will get the number of Habits in the habitList
     * @return integer indicating number of Habits in the list
     */
    public int getCount() {
        return usersList.size();
    }

    public void addUser(User user) {
        usersList.add(user);
    }

    //public void deleteUser(User user) {
        //usersList.remove(user);
   // }

    /**
     * This function will return the specific Habit at the specified index
     * @param index the index of the Habit
     * @return the habit at the certain index
     */
    public User getUser(int index) {
        return usersList.get(index);
    }


    /*public User getUser(String uid) {
        for (User user : usersList) {
            if (user.getUid().equals(uid)) {
                return user;
            }
        }
       return user;
    }*/

    /**
     * This function clears the habit list
     */
    public void clearUserList() {
        this.usersList.clear();
    }

    /**
     * this function will return true if the habit is in the list and false otherwise
     * @param habit The habit to search for in the list
     */
    public Boolean hasUser(Habit habit) {
        return usersList.contains(habit);
    }

    /**
     * Override the equal operation for better comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersList that = (UsersList) o;
        return usersList.equals(that.usersList);
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }
}
