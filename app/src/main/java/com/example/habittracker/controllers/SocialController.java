package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocialController {

    private static class Loader {
        static volatile SocialController INSTANCE = new SocialController();
    }

    private SocialController() {
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static SocialController getInstance() {
        return SocialController.Loader.INSTANCE;
    }

    //Firestore instance
    private final FirebaseFirestore db;
    private final FirebaseUser user;

    /**
     * This function takes a Firestore Requests document snapshot and populates the given requests
     * mapping with the document data. The data in the document should look like the following:
     * {
     *   'outgoing': { [userID]: { status: [pending/accepted/refused], username: [username] }},
     *   'incoming': { [userID]: { status: [pending/accepted/refused], username: [username] }}
     * }
     * @param doc A document snapshot from the Requests collection
     * @param requestMap An instance of RequestMap in which to store the doc data
     */
    public static void convertToRequestMap(DocumentSnapshot doc, RequestMap requestMap) {
        if (doc.exists()) {
            Map<String, Object> docData = doc.getData();
            if (docData != null) {
                requestMap.clearRequestList();
                Log.d("FIRESTORE DATA DEBUG", String.valueOf(docData));
                for (Map.Entry<String, Object> entry : docData.entrySet()) {
                    // data will either be the outgoing requests or the incoming requests object
                    Map<String, Object> data = (Map<String, Object>) entry.getValue();

                    for (Map.Entry<String, Object> requestItem : data.entrySet()) {
                        Map<String, Object> requestData = (Map<String, Object>) requestItem.getValue();
                        Request newRequest = new Request(
                                requestItem.getKey(),
                                (String) requestData.get("status"),
                                (String) requestData.get("username"),
                                ((Timestamp) requestData.get("createdDate")).toDate()
                        );
                        try {
                            requestMap.addRequest(entry.getKey(), newRequest);
                        } catch (Exception e) {
                            Log.e("Firestore", "Request already existed");
                        }
                    }
                }
            }
        }
    }

    public Boolean saveRequest(String type, Request request) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> requestMap = request.getRequestMap();

        // requestTypeMap = { [userId]: requestMap }
//        Map<String, Map<String, Object>> requestTypeMap = new HashMap<>();
//        requestTypeMap.put(request.getUserId(), requestMap);
//
//        // mapping = { "outgoing/incoming": requestTypeMap }
//        Map<String, Map<String, Map<String, Object>>> mapping = new HashMap<>();
//        mapping.put(type, requestTypeMap);

        // update the doc of the user who is submitting the change
        String path = type + "." + request.getUserId();
        Map<String, Object> mapping = new HashMap<>();
        mapping.put(path, requestMap);

        db.collection("Requests").document(this.user.getUid())
                .set(mapping, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

        // update the doc of the user who is subject to the change
        path = flipType(type) + "." + this.user.getUid();
        mapping = new HashMap<>();
        mapping.put(path, requestMap);

        db.collection("Requests").document(request.getUserId())
                .set(mapping, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

        return success.get();
    }

    public Boolean deleteRequest(String type, Request request) {
        AtomicBoolean success = new AtomicBoolean(false);

        // update the doc of the user who is submitting the change
        String path = type + "." + request.getUserId();
        Map<String, Object> updates = new HashMap<>();
        updates.put(path, FieldValue.delete());
        db.collection("Habits").document(this.user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));


        path = flipType(type) + "." + this.user.getUid();
        updates = new HashMap<>();
        updates.put(path, FieldValue.delete());
        db.collection("Habits").document(request.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

        return success.get();
    }

    private static String flipType(String type) {
        if (type.equals("incoming")) {
            return "outgoing";
        } else return "incoming";
    }
}
