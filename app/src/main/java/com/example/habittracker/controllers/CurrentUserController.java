package com.example.habittracker.controllers;

import com.example.habittracker.models.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * This class provides the single point of truth for the current users profile. The currently logged
 * in user can always be retrieved from this controller. This is useful especially in cases where
 * we just need a specific data point from the user profile i.e. username but don't need a whole new
 * listener.
 */
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

    /**
     * Initializes the user profile listener. This functionality NEEDS to be outside the constructor
     * as these values need to be reset on logout.
     */
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


    public User getUser() {
        return this.user;
    }
}
