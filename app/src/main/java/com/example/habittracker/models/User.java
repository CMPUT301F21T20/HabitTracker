package com.example.habittracker.models;


import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User class hold the user information and load user data from firestore
 */
public class User implements Serializable {
    private final String uid;
    private final String username;
    private final String info;


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

    }

    /**
     * Returns a Map of the user object that is compatible with firestore
     * queries
     * @return The Map of the User Object
     */
    public Map<String, Object> getUserMap() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", this.username);
        user.put("info", this.info);
        return user;
    }

    /**
     * Getters and Setters
     */

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getInfo() {
        return info;
    }

}
