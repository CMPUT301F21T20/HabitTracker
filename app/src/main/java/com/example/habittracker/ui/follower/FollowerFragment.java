package com.example.habittracker.ui.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Request;
import com.example.habittracker.models.RequestMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class FollowerFragment extends Fragment {

    private FollowerViewModel followerViewModel;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        followerViewModel =
                new ViewModelProvider(this).get(FollowerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_follower, container, false);
        final ListView followerList = root.findViewById(R.id.following_list);
        followerViewModel.getList().observe(getViewLifecycleOwner(), users -> {
            // update UI
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = new Date();
        RequestMap reqMap = new RequestMap();

        db.collection("Requests").document(uid).addSnapshotListener((docSnapshot, e) -> {
            SocialController.convertToRequestMap(docSnapshot, reqMap);
            RequestListAdapter.notifyDataSetChanged();
        });

        db.collection("Users").document(uid).addSnapshotListener((docSnapshot, e) -> {
//            SocialController.convertToRequestMap(docSnapshot, reqMap);
//            RequestListAdapter.notifyDataSetChanged();
        });


        return root;

    }
}

// LEAVE FOR TESTING FOR NOW
// ID: pSppEsNfvcVMTf5W7BqDm9cY8gm1
// Name: Bobby
// email: 12345678@gmail.com
// pass: Password123


// ID: 8VcwCcLISLMizCUfL08Tfqa4uWG3
// Name: Billy
// email: 87654321@gmail.com
// pass: Password123
