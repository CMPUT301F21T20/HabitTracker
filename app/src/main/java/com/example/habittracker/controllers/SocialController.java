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

import java.util.Date;
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

    /**
     * This function saves an incoming or outgoing request. If the request is incoming and it was
     * accepted then this function will trigger the follow() function which will also update
     * follower/following lists in the users profile. When saving a request, the original request is
     * saved in the originating users document and the inverted request is saved in the target users
     * document. Inverted request just means flipping the type and the user ID (see flipType()).
     * @param request The request to save
     * @return A bool indicating failure or success
     */
    public Boolean saveRequest(String type, Request request) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> requestMap = request.getRequestMap();

        // update the doc of the user who is submitting the change
        String path = type + "." + request.getUserId();
        Map<String, Object> mapping = new HashMap<>();
        mapping.put(path, requestMap);
        dbRequest("Requests", user.getUid(), mapping);

        // update the doc of the user who is subject to the change
        path = flipType(type) + "." + this.user.getUid();
        mapping = new HashMap<>();
        mapping.put(path, requestMap);
        dbRequest("Requests", request.getUserId(), mapping);

        // A status of 'Accepted' and type of 'incoming' means the user has accepted a follow request
        if (request.getStatus().equals("Accepted") && type.equals("incoming")) this.follow(request);

        return success.get();
    }

    /**
     * This function deletes a pending request. If the request is not pending it throws an error. A
     * deleted incoming pending request automatically rejects the incoming request and marks the
     * matching outgoing request as rejected. A deleted outgoing request simply deletes the outgoing
     * request and the incoming request.
     * @param type
     * @param request
     * @return
     */
    public Boolean deleteRequest(String type, Request request) throws Exception {
        if (!request.getStatus().equals("Pending")) throw new Exception("Request must be pending");
        AtomicBoolean success = new AtomicBoolean(false);

        if (type.equals("incoming")) {
            // delete incoming request
            String path = "incoming." + request.getUserId();
            Map<String, Object> updates = new HashMap<>();
            updates.put(path, FieldValue.delete());
            dbRequest("Requests", user.getUid(), updates);

            // mark outgoing request as rejected
            path = "outgoing." + user.getUid();
            updates.clear();
            Request clonedReq = request.cloneRequest();
            clonedReq.setStatus("Rejected");
            clonedReq.setUserId(user.getUid());
            updates.put(path, clonedReq);
            dbRequest("Requests", request.getUserId(), updates);
        } else {
            // delete outgoing request
            String path = "outgoing." + request.getUserId();
            Map<String, Object> updates = new HashMap<>();
            updates.put(path, FieldValue.delete());
            dbRequest("Requests", user.getUid(), updates);

            // delete incoming request
            path = "incoming." + user.getUid();
            updates.clear();
            updates.put(path, FieldValue.delete());
            dbRequest("Requests", request.getUserId(), updates);
        }

        return success.get();
    }

    /**
     * This function adds a follower to the followers list of a user and a following to the following
     * list of the target user. This function is triggered by the saveRequest() function when the
     * type is 'incoming' and the status is 'Accepted'.
     * @param request
     */
    private void follow(Request request) {
        Date saveDate = new Date();

        // update the current users followers in user profile
        Map<String, Object> newFollower = new HashMap<>();
        newFollower.put("followerSince", saveDate);
        String path = "followers." + request.getUserId();
        Map<String, Object> mapping = new HashMap<>();
        mapping.put(path, newFollower);
        dbRequest("Users", user.getUid(), mapping);

        // update the target users (user who request to follow current user) following map
        Map<String, Object> newFollowing = new HashMap<>();
        newFollowing.put("followingSince", saveDate);
        path = "following." + this.user.getUid();
        mapping = new HashMap<>();
        mapping.put(path, newFollowing);
        dbRequest("users", request.getUserId(), mapping);

    }

    /**
     * This function deletes a follower from the followers list and deletes a following from the
     * following list. This function does not change or interact with requests in any way.
     * @param userIdToUnfollow
     * @return
     */
    public void unfollow(String userIdToUnfollow) {
        // delete the following from the current users profile
        String path = "following." + userIdToUnfollow;
        Map<String, Object> updates = new HashMap<>();
        updates.put(path, FieldValue.delete());
        dbRequest("Users", user.getUid(), updates);

        // delete the follower from the target users profile
        path = "followers." + user.getUid();
        updates.clear();
        updates.put(path, FieldValue.delete());
        dbRequest("Users", userIdToUnfollow, updates);
    }


    private static String flipType(String type) {
        if (type.equals("incoming")) return "outgoing";
        else return "incoming";
    }

    private void dbRequest(String colId, String docId, Map<String, Object> data) {
        db.collection(colId).document(docId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
    }
}
