package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.habittracker.adapters.HabitListAdapter;
import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.interfaces.OnHabitListRetrieved;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

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

        final DocumentReference userHabitDocRef = db.collection("Habits").document(uid);

        habitList = new HabitList();
        habitListAdapter = new HabitListAdapter(requireContext(), habitList);
        habitsListView.setAdapter(habitListAdapter);

        HabitListController.getInstance().loadHabitList(uid, new OnHabitListRetrieved() {
            @Override
            public void onHabitListRetrieved(HabitList newHabitList) {
                habitList.clearHabitList();
                for (int i = 0; i < newHabitList.getCount(); i++) {
                    Log.d("HANDLER", i + " -> " + newHabitList.get(i).getTitle());
                    habitList.addHabit(newHabitList.get(i));
                }
                habitListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception taskException) {

            }
        });

        addHabitButton.setOnClickListener(v -> openAddHabitActivity());

        userHabitDocRef.addSnapshotListener((docSnapshot, e) -> {
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

//    public void setHabitList(HabitList habitList) {
//        // copies the ArrayList from one object to the other
////        this.habitList.setHabitList(habitList.getHabitList());
//    }
}