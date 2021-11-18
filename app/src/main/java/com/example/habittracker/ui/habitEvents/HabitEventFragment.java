package com.example.habittracker.ui.habitEvents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HabitEventFragment extends Fragment {
    private HabitEventViewModel habitEventViewModel;
    private CalendarView calendarView;
    private ListView listView;
    private ArrayList<String> events;
    private ArrayAdapter<String> adapter;

    private FirebaseUser user;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitEventViewModel =
                new ViewModelProvider(this).get(HabitEventViewModel.class);
        View root = inflater.inflate(R.layout.fragment_habit_event, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        calendarView = root.findViewById(R.id.calendarHabitEvents);
        listView = root.findViewById(R.id.habitEventList);

        events = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), R.layout.content_habit_event, R.id.habitEventListText, events);
        listView.setAdapter(adapter);

        Date today = new Date();
        getHabitEvents(today.getYear() + 1900,today.getMonth() + 1, today.getDate());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                getHabitEvents(year, month+1, dayOfMonth);
                Log.i("DATE TEST", year  + ", " + (month+1) + ", " + dayOfMonth);
            }
        });

        return root;
    }

    public void getHabitEvents(int year, int month, int day) {
        events.clear();

        db.collection("HabitEvents").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("HABIT EVENT DB", "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> docData = (Map<String, Object>) document.getData();

                        for (String key: docData.keySet()) {
                            Map<String, Object> data = (Map<String, Object>) docData.get(key);
                            Timestamp timestamp = (Timestamp) data.get("date");
                            Date dateCreated = timestamp.toDate();

                            if ((dateCreated.getYear() + 1900) == year && (dateCreated.getMonth() + 1) == month && dateCreated.getDate() == day) {
                                String location = (String) data.get("location");
                                if (location.length() == 0) {
                                    events.add("Habit Event for '" + data.get("habitTitle") + "' recorded");
                                } else {
                                    events.add("Habit Event for '" + data.get("habitTitle") + "' recorded at: " + data.get("location"));
                                }

                            }
                            Log.d("HANDLER", (dateCreated.getYear() + 1900) + ", " + (dateCreated.getMonth() + 1) + ", " + dateCreated.getDate());
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d("HABIT EVENT DB", "No such document");
                    }
                } else {
                    Log.d("HABIT EVENT DB", "get failed with ", task.getException());
                }
            }
        });
    }
}
