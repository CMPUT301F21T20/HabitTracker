package com.example.habittracker.controllers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import com.example.habittracker.interfaces.OnHabitEventDeleted;
import com.example.habittracker.interfaces.OnHabitEventsRetrieved;
import com.example.habittracker.models.Habit.Habit;
import com.example.habittracker.models.HabitEvent.HabitEvent;
import com.example.habittracker.models.HabitEvent.HabitEventList;
import com.example.habittracker.models.Habit.HabitList;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitEventsController {
    private static class Loader {
        static volatile HabitEventsController INSTANCE = new HabitEventsController();
    }

    private HabitEventsController() {
        connect();
    }

    public static HabitEventsController getInstance() {
        return Loader.INSTANCE;
    }

    private FirebaseFirestore DB;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadHabitEvents(String uid, int day, int month, int year, HabitList habitList, OnHabitEventsRetrieved listener) {
        LocalDate docDateName = LocalDate.of(year, month, 2);
        Date legacyDate = Date.from(docDateName.atStartOfDay().toInstant(ZoneOffset.ofHours(18)));
        DB.collection("Users").document(uid).collection("HabitEvents")
                .whereEqualTo("startDate", legacyDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HabitEventList habitEventList = new HabitEventList();
                        try {
                            convertToHabitEventList(task, habitEventList, habitList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listener.onHabitEventsRetrieved(habitEventList);
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void convertToHabitEventList(Task<QuerySnapshot> successfulTask, HabitEventList heList, HabitList habitList) throws Exception {
        heList.clearHabitEventList();
        for (QueryDocumentSnapshot doc : successfulTask.getResult()) {
            Map<String, Object> docData = doc.getData();

            if (docData != null) {
                for (Map.Entry<String, Object> entry : docData.entrySet()) {
                    // skip the startDate value which is just a value used to sort documents by month/year
                    if (entry.getKey().equals("startDate")) continue;
                    Map<String, Object> data = (Map<String, Object>) entry.getValue();
                    Habit matchingHabit = habitList.getHabit((String) data.get("habitId"));
                    Timestamp createdDate = (Timestamp) data.get("createdDate");
                    Timestamp completedDate = (Timestamp) data.get("completedDate");

                    HabitEvent habitEvent = new HabitEvent(
                            matchingHabit,
                            entry.getKey(),
                            matchingHabit.getUserId(), (Boolean) data.get("isCompleted"),
                            (String) data.get("imageStorageNamePrefix"),
                            (String) data.get("location"),
                            (String) data.get("comment"),
                            createdDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                            completedDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    );

                    // Add to habitEvent for easy access to doc for deletion
                    habitEvent.setDocId(doc.getId());

                    heList.addHabitEvent(habitEvent);
                }
            }
        }
    }

    /**
     * Saves a habit event to the corresponding HabitEvent document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the HabitEvent (pre-existing document data won't be overwritten)
     *
     * @param habitEvent The habitEvent to save in Firestore
     * @return True if the operation was successful and false otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean saveHabitEvent(HabitEvent habitEvent) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> habitEventMap = habitEvent.getHabitEventMap();

        // create a mapping within a mapping so merging is possible
        Map<String, Map<String, Object>> mapping = new HashMap<>();
        mapping.put(habitEvent.getHabitEventId(), habitEventMap);

        // create reference to users HabitEvents collection
        CollectionReference colRef = DB.collection("Users").document(habitEvent.getUserId()).collection("HabitEvents");

        LocalDate heDate = habitEvent.getCompletedDate();
        LocalDate docDateName = LocalDate.of(heDate.getYear(), heDate.getMonthValue(), 2);
        Date legacyDate = Date.from(docDateName.atStartOfDay().toInstant(ZoneOffset.ofHours(18)));
        // find the correct HabitEvents document
        colRef.whereEqualTo("startDate", legacyDate).limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            count = count + 1;
                            Log.d("Firestore", document.getId() + " => " + document.getData());
                            colRef
                                    .document(document.getId())
                                    .set(mapping, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        success.set(true);
                                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                    })
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
                        }
                        // if the document does not exist then create it with a random document ID
                        if (count < 1) {
                            Map<String, Date> newDocData = new HashMap<>();
                            newDocData.put("startDate", legacyDate);
                            // create the document with the startDate component
                            colRef
                                    .add(newDocData)
                                    .addOnSuccessListener(docRef -> {
                                        // add the habit event to the document
                                        colRef
                                                .document(docRef.getId())
                                                .set(mapping, SetOptions.merge())
                                                .addOnSuccessListener(bVoid -> {
                                                    success.set(true);
                                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                                })
                                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document"));
                                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                    })
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
        return success.get();
    }

    /**
     * Will delete a habit Event in firestore
     * @param habitEvent the habit event to be deleted
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteHabitEvent(HabitEvent habitEvent) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(habitEvent.getHabitEventId(), FieldValue.delete());

        int year = habitEvent.getCompletedDate().getYear();
        int month = habitEvent.getCompletedDate().getMonthValue();

        LocalDate docDateName = LocalDate.of(year, month, 2);
        Date legacyDate = Date.from(docDateName.atStartOfDay().toInstant(ZoneOffset.ofHours(18)));
        DB.collection("Users").document(habitEvent.getUserId()).collection("HabitEvents")
                .document(habitEvent.getDocId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

    }

    /**
     * Will delete a habit Event in firestore with a callback using a listener
     *@param habitEvent the habit event to be deleted
     * @param listener the listener interface that wil be used to handle callback
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteHabitEventWithCallback(HabitEvent habitEvent, OnHabitEventDeleted listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(habitEvent.getHabitEventId(), FieldValue.delete());

        int year = habitEvent.getCompletedDate().getYear();
        int month = habitEvent.getCompletedDate().getMonthValue();

        LocalDate docDateName = LocalDate.of(year, month, 2);
        Date legacyDate = Date.from(docDateName.atStartOfDay().toInstant(ZoneOffset.ofHours(18)));
        DB.collection("Users").document(habitEvent.getUserId()).collection("HabitEvents")
                .document(habitEvent.getDocId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    listener.onHabitEventDeleted();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

    }

    public void connect() {
        this.DB = FirebaseFirestore.getInstance();
    }

}
