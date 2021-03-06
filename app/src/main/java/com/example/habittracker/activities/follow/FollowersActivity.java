package com.example.habittracker.activities.follow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.activities.UserProfileActivity;
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

public class FollowersActivity extends AppCompatActivity {

    private ImageView back_icon;
    private TextView UserProfileNoFollower_textView;
    private FirebaseFirestore db;
    private User user;
    private ArrayAdapter<Follow> followListAdapter;
    private UsersList usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followers);
        db = FirebaseFirestore.getInstance();
        back_icon = findViewById(R.id.viewHabit_back_icon);
        user = new User();
        usersList = new UsersList();

        final ListView followerListView = findViewById(R.id.followers_list);
        UserProfileNoFollower_textView = findViewById(R.id.UserProfileNoFollower_textView);

        Intent intent = getIntent();
        User intentUser = (User) intent.getSerializableExtra("User");
        if (intentUser != null) {
            // user was passed in intent so don't start snapshot listener

        } else {
            followListAdapter = new FollowersAdapter(getApplicationContext(), user.getFollowers());
            followerListView.setAdapter(followListAdapter);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db.collection("Users").document(uid).addSnapshotListener((docSnapshot, e) -> {
                UsersListController.convertToUser(docSnapshot, user);
                followListAdapter.notifyDataSetChanged();

                ArrayList<String> followUids = user.getFollowers().getFollowUids();
                if (followUids.size() > 0) {
                    db.collection("Users").whereIn(FieldPath.documentId(),
                            followUids)
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
                    UserProfileNoFollower_textView.setVisibility(View.GONE);
                }else{
                    UserProfileNoFollower_textView.setVisibility(View.VISIBLE);
                }
            });
        }

        followerListView.setOnItemClickListener((arg0, view, position, id) -> {
            // load user
            String uid = user.getFollowers().getFollow(position).getUid();
            User tempUser = usersList.getUser(uid);

            Intent tempIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            tempIntent.putExtra("User", tempUser);
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
