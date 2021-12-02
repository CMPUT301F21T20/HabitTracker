package com.example.habittracker.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.habittracker.activities.ViewHabitEventActivity;
import com.example.habittracker.adapters.HabitEventListAdapter;
import com.example.habittracker.models.HabitEvent.HabitEvent;
import com.example.habittracker.models.HabitEvent.HabitEventList;
import com.example.habittracker.models.Habit.HabitList;
import com.example.habittracker.controllers.HabitEventsController;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.interfaces.OnHabitEventsRetrieved;
import com.example.habittracker.interfaces.OnHabitListRetrieved;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This fragment shows the habit events of a specific date
 */
public class HabitEventFragment extends Fragment {
    private View rootView;
    private FloatingActionButton eventPickDate_btn;
    private TextView eventPickDate_TextView;
    private NonScrollListView listView;
    private HabitEventList habitEventsList;
    private HabitEventListAdapter habitEventsAdapter;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private Map<String, String> docIds;

    private String username;
    private int yearSet;
    private int monthSet;
    private int daySet;

    private HabitList habitList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_habit_event, container, false);
        rootView = root;

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        eventPickDate_btn = root.findViewById(R.id.eventPickDate_btn);
        eventPickDate_TextView = root.findViewById(R.id.eventPickDate_TextView);
        listView = root.findViewById(R.id.habitEventList);

        getUsername();

        // Set the date values as today
        setDateToday(eventPickDate_TextView);

        eventPickDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(eventPickDate_TextView);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // when clicking on an item in the list view, open the view habit event activity
                HabitEvent clickedHabitEvent = habitEventsList.get(position);
                if (clickedHabitEvent.getComment().equals("Sent By Developer -> No Habits Today")) {
                    return;
                }
                Intent i = new Intent(getContext(), ViewHabitEventActivity.class);
                i.putExtra("HabitEvent", clickedHabitEvent);
                i.putExtra("HabitList", habitList);
                startActivity(i);
            }
        });

        // Initialize new HashMap containg all docids of the current user's
        // habit events. We will use these to create individual listeners for
        // all the documents
        docIds = new HashMap<String, String>();

        // This listener will be called once, and populate list initially
        db.collection("Habits").document(user.getUid()).collection("HabitEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                // We need Habit List to get Habit Events, retrieve this first then get habit events of today
                // and initialize array adapter inside function
                Log.i("EVENT", "Event happened");
                habitEventsList = new HabitEventList();
                getHabitList();
            }
        });

        return root;
    }

    /**
     * Gets username of the current user
     */
    public void getUsername(){
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Username is stored in firestore, retrieve by querying database
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = (String) document.getData().get("username");
                    } else {
                        Log.d("HABIT EVENT DB", "No such document");
                    }
                } else {
                    Log.d("HABIT EVENT DB", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Will update the textView to display todays date
     * @param eventPickDate_TextView the textView to update
     */
    public void setDateToday(TextView eventPickDate_TextView){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month += 1;

        yearSet = year;
        monthSet = month;
        daySet = day;

        if (day >= 10){
            eventPickDate_TextView.setText(year + " - " + month + " - " + day);
        }else{
            eventPickDate_TextView.setText(year + " - " + month + " - 0" + day);
        }
    }

    /**
     * Open the date picker Dialog to select a certain date
     * @param eventPickDate_TextView the textview to update
     */
    public void datePicker(TextView eventPickDate_TextView) {
        final Calendar c = Calendar.getInstance();
        Date newDate = new Date();

        // Initialize values as current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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

                        getHabitEvents(yearSet,monthSet,daySet);

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
     * Create listeners for each doc for all of the user's habit event docs
     */
    public void setListeners() {
        db.collection("Users").document(user.getUid()).collection("HabitEvents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            docIds.clear();

                            // gather all doc ids in map
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docIds.put(document.getId(), document.getId());
                            }

                            // create listener for each id
                            for (String docId : docIds.keySet()) {
                                db.collection("Users").document(user.getUid())
                                        .collection("HabitEvents").document(docId)
                                        .addSnapshotListener((docSnapshot, e) -> {
                                            getHabitEvents(yearSet, monthSet, daySet);
                                        });
                            }

                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
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

    /**
     * Get Habit Events from Firestore given a date
     * @param year the year of the habit Event
     * @param month the month of the Habit Event
     * @param day the day of the habit Event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitEvents(int year, int month, int day) {
        HabitEventsController habitEventsController = HabitEventsController.getInstance();

        habitEventsController.loadHabitEvents(user.getUid(), day, month, year, habitList, new OnHabitEventsRetrieved() {
            @Override
            public void onHabitEventsRetrieved(HabitEventList newHabitEventList) {
                habitEventsList.clear();
                // Because habits event docs are organized by month, we need to iterate through all retrieved
                // habits and check if date match today
                for (int i = 0; i < newHabitEventList.getCount(); i++) {
                    LocalDate checkDate = newHabitEventList.get(i).getCompletedDate();
                    if (checkDate.getDayOfMonth() == day) {
                        Log.i("ADDING", checkDate.toString());
                        habitEventsList.addHabitEvent(newHabitEventList.get(i));
                    }
                }

                // if no habit events were added add a dummy habie event to show that no habit events were added
                if (habitEventsList.getCount() == 0) {
                    HabitEvent dummyHabitEvent = new HabitEvent();
                    dummyHabitEvent.setComment("Sent By Developer -> No Habits Today");
                    habitEventsList.addHabitEvent(dummyHabitEvent);
                }

                habitEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception taskException) {
                Log.i("ERROR", "Error retrieving habit event list");
            }
        });
    }

    /**
     * First retrieves habit list, then will call the getHabitEvents function when retrieved
     */
    public void getHabitList() {
        HabitListController habitListController = HabitListController.getInstance();
        habitListController.loadHabitList(user.getUid(), new OnHabitListRetrieved() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onHabitListRetrieved(HabitList newHabitList) {
                habitList = newHabitList;
                setDateToday(eventPickDate_TextView);
                getHabitEvents(yearSet,monthSet,daySet);
                habitEventsAdapter = new HabitEventListAdapter(requireContext(), habitEventsList, habitList);
                listView.setAdapter(habitEventsAdapter);
                setListeners();
            }

            @Override
            public void onError(Exception taskException) {
                Log.i("ERROR", "Error retrieving habit list");
            }
        });
    }

    /**
     * Runs when we leave the current fragment
     */
    @Override
    public void onStop() {
        super.onStop();

        // Set the toolbar to be visible
        final androidx.appcompat.widget.Toolbar toolbar = requireActivity().findViewById(R.id.main_toolbar);
        requireActivity().findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_habit_events,
                R.id.navigation_request, R.id.navigation_users, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Update navigaiton
        NavigationUI.setupActionBarWithNavController((AppCompatActivity) requireActivity(), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * Runs when we resume the current fragment
     */
    @Override
    public void onResume() {
        super.onResume();

        // Hide the toolbar
        requireActivity().findViewById(R.id.main_toolbar).setVisibility(View.GONE);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.habitEvent_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }
}
