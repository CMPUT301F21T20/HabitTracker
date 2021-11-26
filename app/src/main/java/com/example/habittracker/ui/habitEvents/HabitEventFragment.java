package com.example.habittracker.ui.habitEvents;

import android.app.DatePickerDialog;
import android.os.Build;
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
import com.example.habittracker.adapters.HabitEventListAdapter;
import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.classes.HabitEventList;
import com.example.habittracker.classes.HabitList;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class HabitEventFragment extends Fragment {
    private View rootView;
    private HabitEventViewModel habitEventViewModel;
    private FloatingActionButton eventPickDate_btn;
    private TextView eventPickDate_TextView;
    private NonScrollListView listView;
    private HabitEventList habitEventsList;
    private HabitEventListAdapter habitEventsAdapter;

    private FirebaseUser user;
    private FirebaseFirestore db;

    private String username;
    private int yearSet;
    private int monthSet;
    private int daySet;

    private HabitList habitList;

//    private int lastVisibleItemPosition = 0;    // mark the last scroll position
//    private boolean scrollFlag = false;         // mark if listview is scrolled

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitEventViewModel =
                new ViewModelProvider(this).get(HabitEventViewModel.class);
        View root = inflater.inflate(R.layout.fragment_habit_event, container, false);
        rootView = root;

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // We need Habit List to get Habit Events, retrieve this first the get habit events of today
        // and initialize array adapter inside function
        getHabitList();

        eventPickDate_btn = root.findViewById(R.id.eventPickDate_btn);
        eventPickDate_TextView = root.findViewById(R.id.eventPickDate_TextView);
        listView = root.findViewById(R.id.habitEventList);

        getUsername();

        eventPickDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(eventPickDate_TextView);
            }
        });

        return root;
    }

    public void getUsername(){
        FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    public String getDateText(Date date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear() + 1900) + "-" +
                (date.getMonth() + 1) + "-" + date.getDate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitEvents(int year, int month, int day) {
        habitEventsList = new HabitEventList();
        HabitEventsController habitEventsController = HabitEventsController.getInstance();

        Log.i("TEST DATE", day + ", " + month + ", " + year);

        habitEventsController.loadHabitEvents(user.getUid(), day, month, year, habitList, new OnHabitEventsRetrieved() {

            @Override
            public void onHabitEventsRetrieved(HabitEventList newHabitEventList) {
                habitEventsList.clear();
                for (int i = 0; i < newHabitEventList.getCount(); i++) {
                    HabitEvent event = newHabitEventList.get(i);
                    Log.i("TEST", event.getCompletedDate().toString());
                    // TODO ADD DATE CHECK
                    habitEventsList.addHabitEvent(newHabitEventList.get(i));
                }
                habitEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception taskException) {
                Log.i("ERROR", "Error retrieving habit event list");
            }
        });

//        HabitEvent habitEvent= new HabitEvent();
//        habitEvent.setCompleted(true);
//        habitEvent.setLocation("Edmonton");
//        habitEvent.setComment("Brief comment");
//        LocalDate date = LocalDate.now();
//        habitEvent.setCreateDate(date);
//        habitEvent.setCompletedDate(date);
//        habitEvent.setImageStorageNamePrefix("1637507374741");
//        habitEventsList.addHabitEvent(habitEvent);
//
//        habitEvent= new HabitEvent();
//        habitEvent.setCompleted(false);
//        habitEvent.setLocation("");
//        habitEvent.setComment("");
//        date = LocalDate.now();
//        habitEvent.setCreateDate(date);
//        habitEvent.setCompletedDate(null);
//        habitEvent.setImageStorageNamePrefix("");
//        habitEventsList.addHabitEvent(habitEvent);
    }

    public void getHabitList() {
        HabitListController habitListController = HabitListController.getInstance();
        habitListController.loadHabitList(user.getUid(), new OnHabitListRetrieved() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onHabitListRetrieved(HabitList newHabitList) {
                Log.i("TEST", "Retrieved habit list with size: " + newHabitList.getCount());
                habitList = newHabitList;
                setDateToday(eventPickDate_TextView);
                getHabitEvents(yearSet,monthSet,daySet);
                habitEventsAdapter = new HabitEventListAdapter(requireContext(), habitEventsList, username);
                listView.setAdapter(habitEventsAdapter);
            }

            @Override
            public void onError(Exception taskException) {
                Log.i("ERROR", "Error retrieving habit list");
            }
        });
    }

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