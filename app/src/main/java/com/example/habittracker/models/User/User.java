package com.example.habittracker.models.User;

import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.Follow.FollowList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User class hold the user information and load user data from firestore
 */
public class User implements Serializable {
    private String uid;
    private String username;
    private String info;
    private final FollowList followers;
    private final FollowList following;

    public User() {
        this.followers = new FollowList("followers");
        this.following = new FollowList("following");
    }

    /**
     * Initialize user with UUID, load user data from firestore
     * @param uid
     * @param username
     * @param info
     */
    public User(String uid, String username, String info) {
        this.uid = uid;
        this.username = username;
        this.info = info;
        this.followers = new FollowList("followers");
        this.following = new FollowList("following");
    }

    public User(String uid, String username, String info, FollowList followers,
                FollowList following) {
        this.uid = uid;
        this.username = username;
        this.info = info;
        this.followers = followers;
        this.following = following;
    }

    public Map<String, Object> getUserMap() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", this.username);
        user.put("info", this.info);
        return user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public FollowList getFollowers() {
        return followers;
    }

    public FollowList getFollowing() {
        return following;
    }
}



