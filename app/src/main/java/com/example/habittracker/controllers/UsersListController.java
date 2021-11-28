package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.classes.User;
import com.example.habittracker.classes.UsersList;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class UsersListController {



    private static class Loader {
        static volatile UsersListController INSTANCE = new UsersListController();
    }

    private UsersListController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static UsersListController getInstance() {return Loader.INSTANCE;}

    private final FirebaseFirestore db;

    public static void convertToUserList(DocumentSnapshot doc, UsersList usersList) {
        if (doc.exists()) {
            Map<String, Object> userData = doc.getData();
            if (userData != null) {
                Log.d("FIRESTORE DATA DEBUG", String.valueOf(userData));
                //usersList.clearUserList();
                //for (Map.Entry<String, Object> entry : docData.entrySet()) {
                    //Map<String, Object> userData = (Map<String, Object>) docData.entrySet().getValue();

                    User user = new User(doc.getId(), (String) userData.get("username"),
                            (String) userData.get("info"));
                    usersList.addUser(user);
               // }

            }
            Log.d("Firestore", "Retrieved habit data");
        } else {
            Log.d("Firestore", "No such document");
        }
    }

}
