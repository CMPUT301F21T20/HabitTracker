package com.example.habittracker.ui.follow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.UserProfileActivity;
import com.example.habittracker.adapters.FollowersAdapter;
import com.example.habittracker.controllers.UsersListController;
import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.User.User;
import com.example.habittracker.models.User.UsersList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {

    private ImageView back_icon;
    private FirebaseFirestore db;
    private User user;
    private ArrayAdapter<Follow> followListAdapter;
    private UsersList usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_following);
        db = FirebaseFirestore.getInstance();
        back_icon = findViewById(R.id.viewHabit_back_icon);
        final ListView followerListView = findViewById(R.id.following_list);

        Intent intent = getIntent();
        User intentUser = (User) intent.getSerializableExtra("User");
        if (intentUser != null) {
            // TODO
        } else {
            user = new User();

            followListAdapter = new FollowersAdapter(getApplicationContext(), user.getFollowing());
            followerListView.setAdapter(followListAdapter);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db.collection("Users").document(uid).addSnapshotListener((docSnapshot, e) -> {
                UsersListController.convertToUser(docSnapshot, user);
                followListAdapter.notifyDataSetChanged();

                ArrayList<String> followUids = user.getFollowing().getFollowUids();
                if (followUids.size() > 0) {
                    db.collection("Users").whereIn(FieldPath.documentId(), followUids)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        UsersListController.convertToUserList(document, usersList);
                                    }
                                } else {
                                    Log.d("Firestore", "Error getting documents: ", task.getException());
                                }
                            });
                }
            });
        }

        followerListView.setOnItemClickListener((arg0, view, position, id) -> {
            // load user
            String uid = user.getFollowing().getFollow(position).getUid();
            User tempUser = usersList.getUser(uid);

            Intent tempIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            tempIntent.putExtra("User", tempUser);
            tempIntent.putExtra("follower", true);
            startActivity(tempIntent);
        });

        back_icon.setOnClickListener(view -> {
            back_icon.setAlpha(0.5f);
            onSupportNavigateUp();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
