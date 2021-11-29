package com.example.habittracker.ui.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;
import com.example.habittracker.ui.requests.FollowViewModel;

public class FollowingFragment extends Fragment {

    private FollowViewModel followViewModel;
    private Button followButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        followViewModel =
                new ViewModelProvider(this).get(FollowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_follow, container, false);
        final ListView userHabits = root.findViewById(R.id.otherHabitList);
        followViewModel.getList().observe(getViewLifecycleOwner(), users -> {
            // update UI
        });
        followButton = root.findViewById(R.id.followButton);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: follow the user after click on the botton
            }
        });

        return root;

    }

}
