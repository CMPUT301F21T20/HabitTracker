package com.example.habittracker.ui.follow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.adapters.FollowersAdapter;
import com.example.habittracker.controllers.UsersListController;
import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FollowingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private User user;
    private ArrayAdapter<Follow> followListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followers);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        user = new User();

        // follower
        View root = getLayoutInflater().inflate(R.layout.activity_view_followers, findViewById(android.R.id.content), false);
        final ListView followerListView = root.findViewById(R.id.follower_list);
        followListAdapter = new FollowersAdapter(getApplicationContext(), user.getFollowing());
        followerListView.setAdapter(followListAdapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(uid).addSnapshotListener((docSnapshot, e) -> {
            UsersListController.convertToUser(docSnapshot, user);
            followListAdapter.notifyDataSetChanged();
            Log.d(">>>>>>>>>>>>>>>>>>>>>>>", user.getFollowers().getFollow(0).getUsername());
        });
    }
}
