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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

    public HabitsTotalFragment() {
    }

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

        final CollectionReference collectionReference = db.collection("Habits");

        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddHabitActivity();
            }
        });

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                handleCollectionUpdate(queryDocumentSnapshots, e);
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
     * handles an update to the list of habits for a certain user
     * @param queryDocumentSnapshots snapshots of the affected documents
     * @param e an exception if raised
     */
    public void handleCollectionUpdate(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HabitList newHabitList = null;
        HabitListController hc = new HabitListController();

        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
        {
            // get the document that lists all habits for the user currently signed in
            if (doc.getId().equals(uid)) {
                Log.d("HANDLER", String.valueOf(doc.getData()));
                Map<String, Object> docData = (Map<String, Object>) doc.getData();
                newHabitList = hc.convertToHabitList(docData, uid);
            }
        }

        // If the user has no habits
        if (newHabitList == null) {
            return;
        }

        habitList.clear();
        for (int i = 0; i < newHabitList.getCount(); i++) {
            Log.d("HANDLER", i + " -> " + newHabitList.get(i).getTitle());
            habitList.addHabit(newHabitList.get(i));
        }
        habitListAdapter.notifyDataSetChanged();
    }
}