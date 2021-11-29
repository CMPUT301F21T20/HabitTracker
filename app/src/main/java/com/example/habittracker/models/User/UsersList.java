package com.example.habittracker.models.User;

import java.util.ArrayList;

/**
 * A class that represents a User list
 */
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

    /**
     * This function adds a user to the list
     * @param user the user to be added
     */
    public void addUser(User user) {
        usersList.add(user);
    }

    /**
     * This function will return the specific User at the specified index
     * @param index the index of the User
     * @return the user at the certain index
     */
    public User getUser(int index) {
        return usersList.get(index);
    }

    /**
     * This function will return the specific User with the specific uid
     * @param uid the id os the user
     * @return the user
     */
    public User getUser(String uid) {
        for (User user : usersList) {
            if (user.getUid().trim().equals(uid.trim())) {
                return user;
            }
        }
       return null;
    }

    /**
     * This function clears the habit list
     */
    public void clearUserList() {
        this.usersList.clear();
    }

    /**
     * this function will return true if the user is in the list and false otherwise
     * @param user The user to search for in the list
     */
    public Boolean hasUser(User user) {
        return usersList.contains(user);
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

    /**
     * Getters and setters
     */

    public ArrayList<User> getUsersList() {
        return usersList;
    }
}
