package com.example.habittracker.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.habittracker.AddNewHabitActivity;
import com.example.habittracker.R;
import com.example.habittracker.ViewHabitActivity;
import com.example.habittracker.controllers.HabitController;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private Context thisContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        thisContext = container.getContext();
        Button testButton = root.findViewById(R.id.viewHabitTestButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewHabitActivity();
            }
        });

        return root;
    }

    public void openViewHabitActivity() {
        Intent intent = new Intent(thisContext, ViewHabitActivity.class);

        Bundle bundle = new Bundle();
        String habitId = "ce5df86c-9712-4bfc-8a2e-eebf9270d291";
        bundle.putString("habitId", habitId);
        bundle.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        intent.putExtras(bundle);
        startActivity(intent);
    }
}