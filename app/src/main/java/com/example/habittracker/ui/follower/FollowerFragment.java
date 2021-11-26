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
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class FollowerFragment extends Fragment {

    private FollowerViewModel followerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        followerViewModel =
                new ViewModelProvider(this).get(FollowerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_follower, container, false);
        final ListView followerList = root.findViewById(R.id.following_list);
        followerViewModel.getList().observe(getViewLifecycleOwner(), users -> {
            // update UI
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date date = new Date();
        SocialController cont = SocialController.getInstance();

        FloatingActionButton addFollowerButton = root.findViewById(R.id.addFollowerButton);
        addFollowerButton.setOnClickListener(v -> {
            // send request from Billy to Bobby
//            cont.saveRequest("outgoing", new Request("pSppEsNfvcVMTf5W7BqDm9cY8gm1", "Pending", "Bobby", date));

            // Bobby "accepts" Billy's request
//            cont.saveRequest("incoming", new Request("8VcwCcLISLMizCUfL08Tfqa4uWG3", "Accepted", "Billy", date));

            // Bobby unfollows Billy
            cont.unfollow("pSppEsNfvcVMTf5W7BqDm9cY8gm1");
        });

        FloatingActionButton deleteFollowerButton = root.findViewById(R.id.deleteFollowerButton);
        deleteFollowerButton.setOnClickListener(v -> {
            try {
                // Billy deletes his request which was also sent to Bobby
//                cont.deleteRequest("outgoing", new Request("pSppEsNfvcVMTf5W7BqDm9cY8gm1", "Pending", "Bobby", date));

                // Bobby deletes the request sent from Billy
                cont.deleteRequest("incoming", new Request("8VcwCcLISLMizCUfL08Tfqa4uWG3", "Pending", "Billy", date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return root;

    }
}

// ID: pSppEsNfvcVMTf5W7BqDm9cY8gm1
// Name: Bobby
// email: 12345678@gmail.com
// pass: Password123


// ID: 8VcwCcLISLMizCUfL08Tfqa4uWG3
// Name: Billy
// email: 87654321@gmail.com
// pass: Password123
