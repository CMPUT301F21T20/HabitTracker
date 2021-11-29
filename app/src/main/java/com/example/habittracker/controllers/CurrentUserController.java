package com.example.habittracker.controllers;

import com.example.habittracker.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CurrentUserController {

    private static class Loader {
        static volatile CurrentUserController INSTANCE = new CurrentUserController();
    }

    public static CurrentUserController getInstance() {
        return CurrentUserController.Loader.INSTANCE;
    }

    private CurrentUserController() {
        this.db = FirebaseFirestore.getInstance();
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.user = new User();

        db.collection("Users").document(fUser.getUid()).addSnapshotListener(((documentSnapshot, e) -> {
            UsersListController.convertToUser(documentSnapshot, user);
        }));
    }

    private final FirebaseFirestore db;
    private final FirebaseUser fUser;
    private User user;

    public User getUser() {
        return this.user;
    }
}
