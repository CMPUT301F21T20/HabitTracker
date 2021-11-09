package com.example.habittracker.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.AddNewHabitActivity;
import com.example.habittracker.R;
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

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FloatingActionButton addHabitButton;
    private Context thisContext;
    private HabitList habitList;
    private ArrayAdapter<Habit> habitListAdapter;
    private ListView habitsListView;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        thisContext = container.getContext();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        addHabitButton = root.findViewById(R.id.addHabitButton);
        habitsListView = root.findViewById(R.id.habits_listview);

        habitList = new HabitList();
        habitListAdapter = new HabitListAdapter(thisContext, habitList);
        habitsListView.setAdapter(habitListAdapter);

        final CollectionReference collectionReference = db.collection("Habits");

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

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
        Intent intent = new Intent(thisContext, AddNewHabitActivity.class);
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