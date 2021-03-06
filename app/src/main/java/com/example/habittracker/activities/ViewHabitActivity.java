package com.example.habittracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.models.Habit.Habit;
import com.example.habittracker.models.Habit.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * This activity is for viewing a habit
 */
public class ViewHabitActivity extends AppCompatActivity {
    private ImageView viewHabit_back_icon;
    private Habit habit;
    private FirebaseFirestore db;
    private TextView titleText;
    private TextView reasonText;
    private TextView startDateText;
    private TextView activeDaysText;
    private TextView viewSharedText;
    private Button editHabitBtn;
    private Button addHabitEventBtn;
    private String activity;
    private TextView streak;
    private TextView highestStreak;
    private int counter;
    //private LocalDate currentDate;
    //private ZoneId defaultZoneId = ZoneId.systemDefault();


    /**
     * Handles functionality when activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        habit = (Habit) intent.getSerializableExtra("Habit");
        activity = (String) intent.getSerializableExtra("pActivity");

        viewHabit_back_icon = findViewById(R.id.viewHabit_back_icon);
        titleText = findViewById(R.id.viewHabitTitle);
        reasonText = findViewById(R.id.viewHabitReason);
        startDateText = findViewById(R.id.viewHabitDateText);
        activeDaysText = findViewById(R.id.viewActiveDaysText);
        viewSharedText = findViewById(R.id.viewSharedText);
        editHabitBtn = findViewById(R.id.editHabitBtn);
        addHabitEventBtn = findViewById(R.id.addHabitEventBtn);
        streak = findViewById(R.id.viewStreakText);
        //highestStreak = findViewById(R.id.viewLongestStreakText);

        /**
         * We ran out of time to fully implement this, we went with a simpler approach
         */
        /*int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // if it is sunday, the returned day above is 1, should be changed to 8
        if (day == 1){
            day = 8;
        }
        // for day, mon is 1, tue is 2, ... , sun is 7
        day -= 1;
        if (day == 0){
            day = 7;
        }

        currentDate = LocalDate.now();
        currentDate = currentDate.minusDays(1);
        Date date = Date.from(currentDate.atStartOfDay(defaultZoneId).toInstant());

        System.out.println(currentDate);
        if(habit.getLastUpdated() == null){
            habit.setStreak(0);
        }
        if(habit.getLastUpdated() != null) {
            System.out.println((habit.getLastUpdated()));
            System.out.println(date);
            if (String.valueOf(habit.getFrequency().get(day - 1)).equals("1") && !(habit.getLastUpdated().equals(date))) {
                System.out.println("I am here progress");

                if (habit.getHighestStreak() < habit.getStreak()) {
                    habit.setHighestStreak(habit.getStreak());
                }
                //habit.setStreak(0);

            }
        }*/



        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));
        streak.setText(String.valueOf(habit.getStreak()));
        //highestStreak.setText(String.valueOf(habit.getHighestStreak()));


        if (habit.getCanShare()){
            viewSharedText.setText("SHARED");
        }else{
            viewSharedText.setText("NOT SHARED");
        }

        editHabitBtn.setOnClickListener(view -> openEditHabitActivity());

        viewHabit_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHabit_back_icon.setAlpha(0.5f);
                onSupportNavigateUp();
            }
        });

        editHabitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditHabitActivity();
            }
        });

        addHabitEventBtn.setOnClickListener(view -> openAddHabitEventActivity());

        // Create a listener to update values when a habit is updated in firestore
        db.collection("Habits").document(habit.getUserId()).addSnapshotListener((docSnapshot, e) -> {
            HabitList newHabitList = new HabitList();
            HabitListController.convertToHabitList(docSnapshot, newHabitList);
            try {
                updateAttributes(newHabitList.getHabit(habit.getHabitId()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        // Since we are reusing the other habit event adapter hide these
        if (activity.equals("UserProfile")) {
            editHabitBtn.setVisibility(View.INVISIBLE);
            addHabitEventBtn.setVisibility(View.INVISIBLE);
        }
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
     * Get the string of active days
     * @param frequency the frequency list from habit class
     * @return the formatted string
     */
    public String getDaysText(ArrayList<Integer> frequency) {
        String out = "";

        // map days of week to frequency list
        String[] mapping = new String[] {"MON", "TUE", "WED", "THU",
                "FRI", "SAT", "SUN"};

        for (int i = 0; i < 7; i++) {
            // Was getting error when comparing integer values, cast to String as a work around
            boolean check = String.valueOf(frequency.get(i)).equals("1");
            if (check) counter += 1;
            if (check && out.length() == 0) {
                // If this is the first day we are adding, don't want comma in front
                out += mapping[i];
            } else if (check) {
                out += ", " + mapping[i];
            }
        }

        return out.length() == 0 ? "No active days selected" : out;
    }

    /**
     * Handles openinng of addNewHabitEventActivtity
     */
    public void openAddHabitEventActivity(){
        Intent intent = new Intent(this, AddNewHabitEventActivity.class);
        intent.putExtra("Habit", habit);
        ViewHabitActivity.this.startActivity(intent);
    }

    /**
     * Handles openinng of editHabitActivtity
     */
    public void openEditHabitActivity(){
        Intent intent = new Intent(this, EditHabitActivity.class);
        intent.putExtra("Habit", habit);
        ViewHabitActivity.this.startActivity(intent);
    }

    /**
     * rewrite the back button on the action bar to make its functionality works better
     * @return false to return to the activity before
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    /**
     * Update all attributes for the view and update habit attribute
     * @param newHabit the habit to update attributes to
     */
    public void updateAttributes(Habit newHabit) {

        habit = newHabit;
        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));
        if (habit.getCanShare()){
            viewSharedText.setText("SHARED");
        } else {
            viewSharedText.setText("NOT SHARED");
        }
    }
}