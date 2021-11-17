package com.example.habittracker.ui.profile;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.LoginActivity;
import com.example.habittracker.R;
import com.example.habittracker.classes.Habit;

import java.util.List;
import com.google.firebase.auth.FirebaseAuth;



public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private Context thisContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.frag_profile, container, false);
        final ListView myHabits = root.findViewById(R.id.habitList);
        profileViewModel.getList().observe(getViewLifecycleOwner(), users -> {

        });
        thisContext = container.getContext();
        Button logoutButton = root.findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
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
