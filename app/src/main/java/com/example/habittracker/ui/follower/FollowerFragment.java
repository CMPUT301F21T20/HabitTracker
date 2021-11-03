package com.example.habittracker.ui.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;

public class FollowerFragment extends Fragment {

    private FollowerViewModel followerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        followerViewModel =
                new ViewModelProvider(this).get(FollowerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_follower, container, false);
        followerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                return;
            }
        });

        return root;

    }
}
