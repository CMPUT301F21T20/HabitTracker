package com.example.habittracker.controllers;

import com.example.habittracker.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class CurrentUserController {

    private static class Loader {
        static volatile CurrentUserController INSTANCE = new CurrentUserController();
    }

    public static CurrentUserController getInstance() {
        return CurrentUserController.Loader.INSTANCE;
    }

    private CurrentUserController() {
        connect();
    }

    private FirebaseFirestore db;
    private FirebaseUser fUser;
    private User user;
    private ListenerRegistration listener;

    public User getUser() {
        return this.user;
    }

    public void connect() {
        if (listener != null) {
            listener.remove();
        }

        this.db = FirebaseFirestore.getInstance();
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.user = new User();

        listener = db.collection("Users").document(fUser.getUid()).addSnapshotListener(((documentSnapshot, e) -> {
            UsersListController.convertToUser(documentSnapshot, user);
        }));
    }
}
