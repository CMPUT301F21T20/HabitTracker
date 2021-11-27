package com.example.habittracker.ui.habitEvents;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habittracker.Plugins.NonScrollListView;
import com.example.habittracker.R;
import com.example.habittracker.adapters.HabitEventListAdapter;
import com.example.habittracker.classes.HabitEventList;
import com.example.habittracker.controllers.HabitEventListController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class HabitEventFragment extends Fragment {
    private View rootView;
    private HabitEventViewModel habitEventViewModel;
    private FloatingActionButton eventPickDate_btn;
    private TextView eventPickDate_TextView;
    private NonScrollListView listView;
    private HabitEventList habitEventsList = new HabitEventList();
    private HabitEventListAdapter habitEventsAdapter;

    private FirebaseUser user;
    private FirebaseFirestore db;

    private String username = "";
    private int yearSet;
    private int monthSet;
    private int daySet;

//    private int lastVisibleItemPosition = 0;    // mark the last scroll position
//    private boolean scrollFlag = false;         // mark if listview is scrolled

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitEventViewModel =
                new ViewModelProvider(this).get(HabitEventViewModel.class);
        View root = inflater.inflate(R.layout.fragment_habit_event, container, false);
        rootView = root;

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("HabitEvents");

        eventPickDate_btn = root.findViewById(R.id.eventPickDate_btn);
        eventPickDate_TextView = root.findViewById(R.id.eventPickDate_TextView);
        listView = root.findViewById(R.id.habitEventList);

        habitEventsAdapter = new HabitEventListAdapter(requireContext(), habitEventsList, username);
        listView.setAdapter(habitEventsAdapter);

//        getUsername();
        setDateToday();
//        getHabitEvents(yearSet,monthSet,daySet);

        eventPickDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(eventPickDate_TextView);
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
     * handles an update to the list of habitEvents for a certain user
     * @param queryDocumentSnapshots snapshots of the affected documents
     * @param e an exception if raised
     */
    public void handleCollectionUpdate(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HabitEventList newHabitEventList = null;
        HabitEventListController hec = new HabitEventListController();

        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
        {
            // get the document that lists all habits for the user currently signed in
            if (doc.getId().equals(uid)) {
                Log.d("HANDLER", String.valueOf(doc.getData()));
                Map<String, Object> docData = (Map<String, Object>) doc.getData();
                newHabitEventList = hec.convertToHabitEventList(docData, uid);
            }
        }

        // If the user has no habits
        if (newHabitEventList == null) {
            return;
        }

        habitEventsList.clear();
        for (int i = 0; i < newHabitEventList.getCount(); i++) {
            habitEventsList.addHabitEvent(newHabitEventList.get(i));
        }
        habitEventsAdapter.notifyDataSetChanged();
    }

//    public void getUsername(){
//        DocumentReference docRef = db.collection("Users").document(user.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        username = (String) document.getData().get("username");
//                        Log.d("Username",username);
//                    } else {
//                        Log.d("HABIT EVENT DB", "No such document");
//                    }
//                } else {
//                    Log.d("HABIT EVENT DB", "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    public void setDateToday(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month += 1;

        yearSet = year;
        monthSet = month;
        daySet = day;

        if (daySet >= 10){
            eventPickDate_TextView.setText(yearSet + " - " + monthSet + " - " + daySet);
        }else{
            eventPickDate_TextView.setText(yearSet + " - " + monthSet + " - 0" + daySet);
        }
    }

    public void datePicker(TextView eventPickDate_TextView) {
        final Calendar c = Calendar.getInstance();
        Date newDate = new Date();

        // Initialize values as current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearSelect, int monthOfYear, int dayOfMonth) {
                        // Update Selected Date
                        newDate.setMonth(monthOfYear);
                        newDate.setYear(yearSelect - 1900);
                        newDate.setDate(dayOfMonth);

                        yearSet = newDate.getYear() + 1900;
                        monthSet = newDate.getMonth() + 1;
                        daySet = newDate.getDate();

                        // Add 1900 to year, as the getYear function returns year - 1900
                        if(newDate.getDate() >= 10) {
                            eventPickDate_TextView.setText((newDate.getYear() + 1900) + " - " +
                                    (newDate.getMonth() + 1) + " - " + newDate.getDate());
                        }else{
                            eventPickDate_TextView.setText((newDate.getYear() + 1900) + "-" +
                                    (newDate.getMonth() + 1) + " - 0" + newDate.getDate());
                        }

//                        getHabitEvents(yearSet,monthSet,daySet);

                        Animation anim = new AlphaAnimation(0.0f,0.1f);
                        anim.setDuration(150);
                        anim.setStartOffset(100);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.ABSOLUTE);
                        eventPickDate_TextView.startAnimation(anim);
                    }
                }, year, month, day);
        datePickerDialog.show();
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

//    public void getHabitEvents(int year, int month, int day) {
//        // create a new habit event list for new-chosen date
//        habitEventsList.clear();
//
//        HabitEvent habitEvent = new HabitEvent();
//
//        DocumentReference habitEvents = db.collection("HabitEvents").document(user.getUid());
//
//        Task<DocumentSnapshot> documentSnapshotTask = habitEvents.get();
//
//        documentSnapshotTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d("HABIT EVENT DB", "DocumentSnapshot data: " + document.getData());
//                        Map<String, Object> docData = (Map<String, Object>) document.getData();
//
//                        for (String key: docData.keySet()) {
//                            habitEvent.clear();
//                            Map<String, Object> data = (Map<String, Object>) docData.get(key);
//                            Timestamp timestamp;
//                            Timestamp timestampCompleted = (Timestamp) data.get("completedDate");
//                            Timestamp timestampCreated = (Timestamp) data.get("createdDate");
//                            timestamp = timestampCompleted;
//                            Date completedDate = null;
//                            if (timestampCompleted == null){
//                                timestamp = timestampCreated;
//                            }else{
//                                completedDate = timestampCompleted.toDate();
//                            }
//                            Date createdDate = timestampCreated.toDate();
//                            Date dateOfEvent = timestamp.toDate();
//
//                            if ((dateOfEvent.getYear() + 1900) == year && (dateOfEvent.getMonth() + 1) == month && dateOfEvent.getDate() == day) {
//                                String location = "";
//                                String comment = "";
//                                String imageStorageNamePrefix = "";
//                                location = (String) data.get("location");
//                                comment = (String) data.get("comment");
//                                boolean isCompleted = (boolean) data.get("isCompleted");
//                                imageStorageNamePrefix = (String) data.get("imageStorageNamePrefix");
//
//                                habitEvent.setUserId(user.getUid());
//                                habitEvent.setHabitEventId(key);
//                                habitEvent.setCompleted(isCompleted);
//                                habitEvent.setLocation(location);
//                                habitEvent.setComment(comment);
//                                habitEvent.setImageStorageNamePrefix(imageStorageNamePrefix);
//                                habitEvent.setCompletedDate(completedDate);
//                                habitEvent.setCreateDate(createdDate);
//
//                                habitEventsList.addHabitEvent(habitEvent);
//                            }
//                            Log.d("HANDLER", (dateOfEvent.getYear() + 1900) + ", " + (dateOfEvent.getMonth() + 1) + ", " + dateOfEvent.getDate());
//                        }
//
//                        if (habitEventsAdapter != null)
//                            habitEventsAdapter.notifyDataSetChanged();
//
//                    } else {
//                        Log.d("HABIT EVENT DB", "No such document");
//                    }
//                } else {
//                    Log.d("HABIT EVENT DB", "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    @Override
    public void onStop() {
        super.onStop();
        final androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.main_toolbar);
        requireActivity().findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_habit_events,
                R.id.navigation_following, R.id.navigation_follower, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController((AppCompatActivity) requireActivity(), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().findViewById(R.id.main_toolbar).setVisibility(View.GONE);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.habitEvent_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }
}
