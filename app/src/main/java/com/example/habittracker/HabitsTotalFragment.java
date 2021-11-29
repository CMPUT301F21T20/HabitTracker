package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.habittracker.adapters.HabitListAdapter;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class HabitsTotalFragment extends Fragment {

    private TextView habitsListPrompt_textView;
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
        habitsListPrompt_textView = root.findViewById(R.id.habitsListPrompt_textView);

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
            if (habitList.getCount() == 0){
                habitsListPrompt_textView.setVisibility(View.VISIBLE);
            }else{
                habitsListPrompt_textView.setVisibility(View.GONE);
            }
            habitListAdapter.notifyDataSetChanged();
        });

        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                Habit habit = (Habit) habitList.getHabit(position);
                openViewHabitActivity(habit);

            }
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

    /**
     * Opens the view habit activity
     * @param habit the habit to display information about
     */
    public void openViewHabitActivity(Habit habit) {
        Intent i = new Intent(requireContext(), ViewHabitActivity.class);
        i.putExtra("Habit", habit);
        i.putExtra("pActivity", "HabitsTotal");
        startActivity(i);
    }
}