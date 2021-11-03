package com.example.habittracker.ui.following;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;

public class FollowFragment extends Fragment {

    private FollowViewModel followViewModel;
    private Button followButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        followViewModel =
                new ViewModelProvider(this).get(FollowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_follow, container, false);
        followViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                return;
            }
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
