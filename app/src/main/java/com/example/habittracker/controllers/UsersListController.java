package com.example.habittracker.controllers;

import android.util.Log;

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

    public static UsersListController getInstance() {return Loader.INSTANCE;}

    private final FirebaseFirestore db;
    //private FirebaseAuth fAuth;


    public static void convertToUserList(DocumentSnapshot doc, UsersList usersList) {
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if (doc.exists()) {
            Map<String, Object> userData = doc.getData();
            if (userData != null && !(doc.getId().equals(fUser.getUid()))) {
                //Log.d("FIRESTORE DATA DEBUG", String.valueOf(userData));

                    User user = new User(doc.getId(),
                            (String) userData.get("username"),
                            (String) userData.get("info"),
                            (Map<String, Map<String, Object>>) userData.get("followers"),
                            (Map<String, Map<String, Object>>) userData.get("following")
                    );

                    usersList.addUser(user);


            }
            Log.d("Firestore", "Retrieved habit data");
        } else {
            Log.d("Firestore", "No such document");
        }
    }
}
