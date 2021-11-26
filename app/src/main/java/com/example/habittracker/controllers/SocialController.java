package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SocialController {

    private static class Loader {
        static volatile SocialController INSTANCE = new SocialController();
    }

    private SocialController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static SocialController getInstance() {
        return SocialController.Loader.INSTANCE;
    }

    //Firestore instance
    private final FirebaseFirestore db;

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
                                (String) requestData.get("username")
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
}
