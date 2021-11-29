package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.Follow.FollowList;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;
import com.example.habittracker.models.User;
import com.example.habittracker.models.UsersList;
import com.example.habittracker.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class UsersListController {

    private static FirebaseAuth fAuth;

    private static class Loader {
        static volatile UsersListController INSTANCE = new UsersListController();
    }

    private UsersListController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static UsersListController getInstance() {
        return Loader.INSTANCE;
    }

    private final FirebaseFirestore db;
    //private FirebaseAuth fAuth;


    public static void convertToUserList(DocumentSnapshot doc, UsersList usersList) {
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (doc.exists()) {
            Map<String, Object> userData = doc.getData();
            if (userData != null && !(doc.getId().equals(fUser.getUid()))) {
                User user = new User(
                        doc.getId(),
                        (String) userData.get("username"),
                        (String) userData.get("info")
                );
                if (userData.get("followers") != null) {
                    convertToFollowList((Map<String, Map<String, Object>>) userData.get("followers"),
                            user.getFollowers());
                }
                if (userData.get("following") != null) {
                    convertToFollowList((Map<String, Map<String, Object>>) userData.get("following"),
                            user.getFollowing());
                }
                usersList.addUser(user);
            }
            Log.d("Firestore", "Retrieved habit data");
        } else {
            Log.d("Firestore", "No such document");
        }
    }

    public static void convertToUser(DocumentSnapshot doc, User user) {
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (doc.exists()) {
            Map<String, Object> userData = doc.getData();
            if (userData != null && !(doc.getId().equals(fUser.getUid()))) {
                user.setUid(doc.getId());
                user.setUsername((String) userData.get("username"));
                user.setInfo((String) userData.get("info"));
                if (userData.get("followers") != null) {
                    convertToFollowList((Map<String, Map<String, Object>>) userData.get("followers"),
                            user.getFollowers());
                }
                if (userData.get("following") != null) {
                    convertToFollowList((Map<String, Map<String, Object>>) userData.get("following"),
                            user.getFollowing());
                }
            }
            Log.d("Firestore", "Retrieved habit data");
        } else {
            Log.d("Firestore", "No such document");
        }
    }

    private static void convertToFollowList(Map<String, Map<String, Object>> follows, FollowList followList) {
        followList.clearFollowList();
        for (Map.Entry<String, Map<String, Object>> entry : follows.entrySet()) {
            Map<String, Object> followData = (Map<String, Object>) entry.getValue();
            Follow follow = new Follow(
                    entry.getKey(),
                    (String) followData.get("username"),
                    ((Timestamp) followData.get("since")).toDate()
            );
            followList.addFollow(follow);
        }
    }
}
