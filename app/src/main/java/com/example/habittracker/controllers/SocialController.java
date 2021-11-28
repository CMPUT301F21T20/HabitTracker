package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;
import com.example.habittracker.models.User;
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

        db.collection("Users").document(this.user.getUid())
            .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Map<String, Object> userData = document.getData();
                        username = (String) userData.get("username");
                        Log.d("Firestore", "DocumentSnapshot data: " + userData);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
        });
    }

    public static SocialController getInstance() {
        return SocialController.Loader.INSTANCE;
    }

    //Firestore instance
    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private String username;

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
     * document. Inverted request just means flipping the type, user ID, and username.
     * @param type
     * @param request The request to save
     * @return A bool indicating failure or success
     */
    public Boolean saveRequest(String type, Request request) {
        AtomicBoolean success = new AtomicBoolean(false);

        // create needed maps
        Map<String, Object> userRequestMap = new HashMap<>();
        Map<String, Map<String, Object>> mapping = new HashMap<>();

        // update the doc of the user who is submitting the change
        userRequestMap.put(request.getUserId(), request.getRequestMap());
        mapping.put(type, userRequestMap);
        dbRequest("Requests", user.getUid(), mapping);

        // clear maps
        userRequestMap.clear();
        mapping.clear();

        // update the doc of the user who is subject to the change
        Request flippedRequest = flipRequest(request);
        userRequestMap.put(flippedRequest.getUserId(), flippedRequest.getRequestMap());
        mapping.put(flipType(type), userRequestMap);
        dbRequest("Requests", request.getUserId(), mapping);

        // A status of 'Accepted' and type of 'incoming' means the user has accepted a follow request
        if (request.getStatus().equals("Accepted") && type.equals("incoming")) this.follow(request);

        return success.get();
    }

    /**
     * This function deletes a pending request. If the request is not pending it throws an error. A
     * deleted incoming pending request automatically rejects the incoming request and marks the
     * matching outgoing request as rejected. A deleted outgoing request simply deletes the outgoing
     * request and the incoming request. NOTE: This function has side effects so it is not mean to
     * be used to delete any random request. It is meant to be triggered directly by the delete
     * button of a specific request on the UI.
     * @param type
     * @param request
     * @return
     */
    public Boolean deleteRequest(String type, Request request) throws Exception {
        if (!request.getStatus().equals("Pending")) throw new Exception("Request must be pending");
        AtomicBoolean success = new AtomicBoolean(false);

        Map<String, Map<String, Object>> mapping = new HashMap<>();
        Map<String, Object> userRequestMap = new HashMap<>();

        if (type.equals("incoming")) {
            // delete incoming request
            userRequestMap.put(request.getUserId(), FieldValue.delete());
            mapping.put("incoming", userRequestMap);
            dbRequest("Requests", user.getUid(), mapping);

            mapping.clear();
            userRequestMap.clear();

            // mark outgoing request as rejected
            Request flippedRequest = flipRequest(request);
            flippedRequest.setStatus("Rejected");
            userRequestMap.put(flippedRequest.getUserId(), flippedRequest.getRequestMap());
            mapping.put("outgoing", userRequestMap);
            dbRequest("Requests", request.getUserId(), mapping);
        } else {
            // delete outgoing request
            userRequestMap.put(request.getUserId(), FieldValue.delete());
            mapping.put("outgoing", userRequestMap);
            dbRequest("Requests", user.getUid(), mapping);

            mapping.clear();
            userRequestMap.clear();

            // delete incoming request
            userRequestMap.put(user.getUid(), FieldValue.delete());
            mapping.put("incoming", userRequestMap);
            dbRequest("Requests", request.getUserId(), mapping);
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

        Map<String, Map<String, Object>> mapping = new HashMap<>();
        Map<String, Object> follower = new HashMap<>();
        Map<String, Object> followerData = new HashMap<>();

        // update the current users followers in user profile
        followerData.put("followerSince", saveDate);
        follower.put(request.getUserId(), followerData);
        mapping.put("followers", follower);
        dbRequest("Users", user.getUid(), mapping);

        mapping.clear();
        follower.clear();
        followerData.clear();

        // update the target users (user who request to follow current user) following map
        followerData.put("followingSince", saveDate);
        follower.put(user.getUid(), followerData);
        mapping.put("following", follower);
        dbRequest("Users", request.getUserId(), mapping);
    }

    /**
     * This function deletes a follower from the followers list and deletes a following from the
     * following list. This function also deletes both requests.
     * @param userIdToUnfollow
     * @return
     */
    public void unfollow(String userIdToUnfollow) {
        Map<String, Map<String, Object>> mapping = new HashMap<>();
        Map<String, Object> follower = new HashMap<>();

        // delete the following from the current users profile
        follower.put(userIdToUnfollow, FieldValue.delete());
        mapping.put("following", follower);
        dbRequest("Users", user.getUid(), mapping);

        mapping.clear();
        follower.clear();

        // delete the request from the current users profile
        follower.put(userIdToUnfollow, FieldValue.delete());
        mapping.put("outgoing", follower);
        dbRequest("Requests", user.getUid(), mapping);

        mapping.clear();
        follower.clear();

        // delete the follower from the target users profile
        follower.put(user.getUid(), FieldValue.delete());
        mapping.put("followers", follower);
        dbRequest("Users", userIdToUnfollow, mapping);

        mapping.clear();
        follower.clear();

        // delete the request from the target users profile
        follower.put(user.getUid(), FieldValue.delete());
        mapping.put("incoming", follower);
        dbRequest("Requests", userIdToUnfollow, mapping);
    }


    private static String flipType(String type) {
        if (type.equals("incoming")) return "outgoing";
        else return "incoming";
    }

    private Request flipRequest(Request request) {
        Request clone = request.cloneRequest();
        clone.setUserId(user.getUid());
        clone.setUserName(username);
        return clone;
    }

    private void dbRequest(String colId, String docId, Map<String, Map<String, Object>> data) {
        db.collection(colId).document(docId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
    }
}


