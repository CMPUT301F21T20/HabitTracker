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
//    private HabitList habitList;


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

    public Map<String, Object> getUserMap() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", this.username);
        user.put("info", this.info);
        return user;
    }

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
