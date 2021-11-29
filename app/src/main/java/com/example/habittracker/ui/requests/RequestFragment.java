package com.example.habittracker.ui.requests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habittracker.R;
import com.example.habittracker.adapters.FollowersAdapter;
import com.example.habittracker.controllers.UsersListController;
import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class RequestFragment extends Fragment {

    private FirebaseFirestore db;
    private User user;
    private ArrayAdapter<Follow> followListAdapter;
    private ListView followListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        user = new User();

        View root = inflater.inflate(R.layout.fragment_follower, container, false);
        followListView = root.findViewById(R.id.following_list);

        followListAdapter = new FollowersAdapter(requireContext(), user.getFollowers());
        followListView.setAdapter(followListAdapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(uid).addSnapshotListener((docSnapshot, e) -> {
            UsersListController.convertToUser(docSnapshot, user);
            followListAdapter.notifyDataSetChanged();
        });

        return root;

    }
}
