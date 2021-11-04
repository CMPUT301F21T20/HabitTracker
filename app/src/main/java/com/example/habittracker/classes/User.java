package com.example.habittracker.classes;


import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    private final String uid;
//    private HabitList habitList;
    private FirebaseFirestore db;
    private DocumentReference _userDocRef;
    private Map<String, Object> _userData;
    private boolean _dataLoaded;

    public User(String uid) {
        this.uid = uid;
        db = FirebaseFirestore.getInstance();

        // load user data from the user doc in firestore
        _dataLoaded = loadUserData();
        if (!_dataLoaded) {
            // TODO: connection to firestore failure
        }

//        this.habitList = new HabitList();

        // get current habit list once created in firebase
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return (String) _userData.get("username");
    }

    public String getInfo() {
        return (String) _userData.get("info");
    }

    public boolean isDataLoaded() {
        return _dataLoaded;
    }

    /**
     * This function loads the user document from firestore. It sets the resulting data to
     * @return
     */
    public boolean loadUserData() {
        // load user data from the user doc in firestore
        AtomicBoolean success = new AtomicBoolean(false);
        _userDocRef = db.collection("Users").document(this.uid);
        _userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    _userData = document.getData();
                    Log.d("Firestore", "DocumentSnapshot data: " + _userData);
                    success.set(true);
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
        return success.get();
    }

    /**
     * This function updates the info of a users profile in firestore.
     * @param newInfo The new info string to save
     * @return true if successful and false otherwise
     */
    public boolean updateInfo(String newInfo) {
        // TODO: newInfo max length/vulgar checks
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(uid)
                    .update("info", newInfo)
                    .addOnSuccessListener(aVoid -> {
                        success.set(true);
                        _userData.put("info", newInfo);
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error writing document", e);
                    });
        } catch (Exception error) {
            Log.w("Firestore", error);
        }

        return success.get();
    }

    public void addFollower() {}
    public void deleteFollower() {}
    public void follow() {}
    public void unfollow() {}

}
