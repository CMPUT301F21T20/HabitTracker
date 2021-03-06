package com.example.habittracker.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habittracker.R;
import com.example.habittracker.activities.LoginActivity;
import com.example.habittracker.activities.follow.FollowersActivity;
import com.example.habittracker.activities.follow.FollowingActivity;
import com.example.habittracker.controllers.CurrentUserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private Context thisContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView followers_counter = root.findViewById(R.id.followers_counter);
        TextView following_counter = root.findViewById(R.id.following_counter);

        followers_counter.setText(String.valueOf(CurrentUserController.getInstance().getUser().getFollowers().getFollowList().size()));
        following_counter.setText(String.valueOf(CurrentUserController.getInstance().getUser().getFollowing().getFollowList().size()));

        TextView emailtext = root.findViewById(R.id.profile_email);
        emailtext.setText(fUser.getEmail());

        thisContext = container.getContext();
        Button logoutButton = root.findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        });

        LinearLayout followerBtn = root.findViewById(R.id.FollowersButton);
        followerBtn.setOnClickListener(view -> {
            // open followers activity
            Intent i = new Intent(requireContext(), FollowersActivity.class);
            startActivity(i);
        });

        LinearLayout followingBtn = root.findViewById(R.id.FollowingButton);
        followingBtn.setOnClickListener(view -> {
            // open following activity
            Intent i = new Intent(requireContext(), FollowingActivity.class);
            startActivity(i);
        });

        DocumentReference docRef = db.collection("Users").document(fUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    TextView name = root.findViewById(R.id.profileName);
                    name.setText((String) document.getData().get("username"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        return root;
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(thisContext, LoginActivity.class));
        getActivity().finish();
    }


}
