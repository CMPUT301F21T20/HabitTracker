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
import java.util.Date;
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
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_habits_today, container, false);

        habitsTodayListView = root.findViewById(R.id.habitsToday_listview);

        habitList = new HabitList();
        habitListAdapter = new HabitListAdapter(requireContext(), habitList);
        habitsTodayListView.setAdapter(habitListAdapter);

        final CollectionReference collectionReference = db.collection("Habits");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                handleCollectionUpdate(queryDocumentSnapshots, e);
            }
        });

        return root;
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

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // if it is sunday, the returned day above is 1, should be changed to 8
        if (day == 1){
            day = 8;
        }

        int minus = 0;
        for (int i = 0; i < newHabitList.getCount(); i++){
            // if the habit is not repeated today, drop the habit
            if (("" + newHabitList.get(i - minus).getFrequency().get(day - 2)).equals("0")){
                newHabitList.deleteHabit(i - minus);
                minus += 1;
            }else{
                // if the habit is started later than today, drop it
                if (newHabitList.get(i - minus).getDateCreated().getTime() > System.currentTimeMillis()) {
                    newHabitList.deleteHabit(i - minus);
                    minus += 1;
                }
            }
        }

        habitList.clear();
        for (int i = 0; i < newHabitList.getCount(); i++) {
            Log.d("HANDLER", i + " -> " + newHabitList.get(i).getTitle());
            habitList.addHabit(newHabitList.get(i));
        }
        habitListAdapter.notifyDataSetChanged();
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    public String getDateText(Date date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear() + 1900) + "-" +
                (date.getMonth() + 1) + "-" + date.getDate();
    }
}