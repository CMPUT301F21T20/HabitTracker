package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.HabitEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitEventController {
    private final FirebaseFirestore DB;

    public HabitEventController() {
        this.DB = FirebaseFirestore.getInstance();
    }

    /**
     * Saves a habitevent to the corresponding habitEvent document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the habitEvent (pre-existing document data won't be overwritten)
     *
     * @param habitEvent The habitEvent to save in Firestore
     * @return True if the operation was successful and false otherwise
     */
    public Boolean saveHabitEvent(HabitEvent habitEvent) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> habitEventMap = habitEvent.getHabitEventMap();

        // create a mapping within a mapping so merging is possible
        Map<String, Map<String, Object>> mapping = new HashMap<>();
        mapping.put(habitEvent.getHabitEventId(), habitEventMap);

        // save the new habitEvent and create doc if it doesn't already exists
        DB.collection("HabitEvents").document(habitEvent.getUserId())
                .set(mapping, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        return success.get();
    }

    /**
     * Deletes a habitEvent from the corresponding habitEvent document in Firestore.
     *
     * @param habitEvent The habitEvent to delete
     * @return True if the operation was successful and false otherwise
     */
    public Boolean deleteHabitEvent(HabitEvent habitEvent) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put(habitEvent.getHabitEventId(), FieldValue.delete());
        DB.collection("HabitEvents").document(habitEvent.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        if (!success.get()){
            return false;
        }else{
            success.set(false);
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("HabitEventImages_" + habitEvent.getUserId() +
                "/" + habitEvent.getImageStorageNamePrefix() + ".jpg");
        photoReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                success.set(true);
                Log.d("Firestore", "Photo successfully deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success.set(false);
                Log.d("Firestore","Error deleting photo in storage");
            }
        });
        return success.get();
    }
}
