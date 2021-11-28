package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.habittracker.adapters.HabitListAdapter;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class HabitsTotalFragment extends Fragment {

    private HabitList habitList;
    private ArrayAdapter<Habit> habitListAdapter;
    private ListView habitsListView;
    private FloatingActionButton addHabitButton;
    private FirebaseFirestore db;

    public HabitsTotalFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_habits_total, container, false);

        addHabitButton = root.findViewById(R.id.addHabitButton);
        habitsListView = root.findViewById(R.id.habits_listview);

        habitList = new HabitList();
        habitListAdapter = new HabitListAdapter(requireContext(), habitList);
        habitsListView.setAdapter(habitListAdapter);

        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddHabitActivity();
            }
        });

        db.collection("Habits").document(uid).addSnapshotListener((docSnapshot, e) -> {
            HabitListController.convertToHabitList(docSnapshot, habitList);
            habitListAdapter.notifyDataSetChanged();
        });

        return root;
    }

    /**
     * This function will handle opening the add new habit activity
     */
    public void openAddHabitActivity() {
        Intent intent = new Intent(requireContext(), AddNewHabitActivity.class);
        startActivity(intent);
    }
}