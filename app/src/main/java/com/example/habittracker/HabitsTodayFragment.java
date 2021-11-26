package com.example.habittracker;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.habittracker.adapters.HabitListAdapter;
import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Map;

public class HabitsTodayFragment extends Fragment {

    private HabitList habitList;
    private ArrayAdapter<Habit> habitListAdapter;
    private ListView habitsTodayListView;
    private FirebaseFirestore db;

    public HabitsTodayFragment() {
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
        View root = inflater.inflate(R.layout.fragment_habits_today, container, false);

        habitsTodayListView = root.findViewById(R.id.habitsToday_listview);

        habitList = new HabitList();
        habitListAdapter = new HabitListAdapter(requireContext(), habitList);
        habitsTodayListView.setAdapter(habitListAdapter);

        db.collection("Habits").document(uid).addSnapshotListener((docSnapshot, e) -> {
            HabitListController.convertToHabitList(docSnapshot, habitList);

            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            // if it is sunday, the returned day above is 1, should be changed to 8
            if (day == 1){
                day = 8;
            }

            HabitList hl = new HabitList();

            for (int i = 0; i < habitList.getHabitList().size(); i++) {
                String title = habitList.getHabit(i).getTitle();
                boolean isSameDay = ("" + habitList.getHabit(i).getFrequency().get(day - 2)).equals("1");
                boolean isAfter = habitList.getHabit(i).getDateCreated().getTime() <= System.currentTimeMillis();
                if (isSameDay && isAfter){
                    hl.addHabit(habitList.getHabit(i));
                }
            }

            habitList.clearHabitList();
            for (int i = 0; i < hl.getCount(); i++) {
                Log.d("HANDLER", i + " -> " + hl.getHabit(i).getTitle());
                habitList.addHabit(hl.getHabit(i));
            }

            habitListAdapter.notifyDataSetChanged();
        });

        return root;
    }
}