package com.example.habittracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.habittracker.adapters.HabitEventListAdapter;
import com.example.habittracker.controllers.HabitEventsController;
import com.example.habittracker.interfaces.OnHabitEventsRetrieved;
import com.example.habittracker.models.DownloadImageTask;
import com.example.habittracker.models.HabitEvent;
import com.example.habittracker.models.HabitEventList;
import com.example.habittracker.models.HabitList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class ViewHabitEventActivity extends AppCompatActivity {
    private ImageView viewHabitEvent_back_icon;
    private TextView habitTile;
    private TextView habitText;
    private TextView dateCompletedTitle;
    private TextView dateCompletedText;
    private TextView dateRecordedTitle;
    private TextView dateRecordedText;
    private TextView pictureTitle;
    private ImageView picture;
    private TextView locationTitle;
    private TextView locationTitleText;
    private TextView commentTitle;
    private TextView commentText;
    private Button editHabitEventButton;

    private HabitEvent habitEvent;
    private HabitList habitList;

    private FirebaseUser user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit_event);

        Intent intent = getIntent();

        habitEvent = (HabitEvent) intent.getSerializableExtra("HabitEvent");
        habitList = (HabitList) intent.getSerializableExtra("HabitList");

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        viewHabitEvent_back_icon = findViewById(R.id.viewHabitEvent_back_icon);
        habitTile = findViewById(R.id.habitEvent_habitTitle);
        habitText = findViewById(R.id.habitEvent_habitTitle_text);
        dateCompletedTitle = findViewById(R.id.habitEvent_DateCompleted);
        dateCompletedText = findViewById(R.id.habitEvent_DateCompleted_text);
        dateRecordedTitle = findViewById(R.id.habitEvent_DateRecorded);
        dateRecordedText = findViewById(R.id.habitEvent_DateRecorded_text);
        pictureTitle = findViewById(R.id.habitEvent_Picture);
        picture = findViewById(R.id.habitEventImage);
        locationTitle = findViewById(R.id.habitEvent_Location);
        locationTitleText = findViewById(R.id.habitEvent_Location_text);
        commentTitle = findViewById(R.id.habitEvent_Comment);
        commentText = findViewById(R.id.habitEvent_Comment_text);
        editHabitEventButton = findViewById(R.id.editHabitEventBtn);

        habitTile.setVisibility(View.VISIBLE);
        habitText.setVisibility(View.VISIBLE);
        dateCompletedTitle.setVisibility(View.VISIBLE);
        dateCompletedText.setVisibility(View.VISIBLE);
        dateRecordedTitle.setVisibility(View.VISIBLE);
        dateRecordedText.setVisibility(View.VISIBLE);
        pictureTitle.setVisibility(View.VISIBLE);
        picture.setVisibility(View.VISIBLE);
        locationTitle.setVisibility(View.VISIBLE);
        locationTitleText.setVisibility(View.VISIBLE);
        commentTitle.setVisibility(View.VISIBLE);
        commentText.setVisibility(View.VISIBLE);

        setAttributes();

        viewHabitEvent_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHabitEvent_back_icon.setAlpha(0.5f);
                finish();
            }
        });

        editHabitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditHabitEvent();
            }
        });
    }

    /**
     * Sets fields of the view
     */
    public void setAttributes() {
        habitText.setText(habitEvent.getHabit().getTitle());

        if (habitEvent.getCompletedDate() != null) {
            dateCompletedText.setText(getDateText(habitEvent.getCompletedDate()));
        } else {
            dateCompletedTitle.setVisibility(View.GONE);
            dateCompletedText.setVisibility(View.GONE);
        }

        dateRecordedText.setText(getDateText(habitEvent.getCreateDate()));

        if (habitEvent.getImageStorageNamePrefix().length() != 0){
            new DownloadImageTask(picture).execute(habitEvent.getImageStorageNamePrefix());
        } else {
            pictureTitle.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
        }

        if (habitEvent.getLocation().length() != 0) {
            locationTitleText.setText(habitEvent.getLocation());
        } else {
            locationTitle.setVisibility(View.GONE);
            locationTitleText.setVisibility(View.GONE);
        }

        if (habitEvent.getComment().length() != 0) {
            commentText.setText(habitEvent.getComment());
        }else{
            commentTitle.setVisibility(View.GONE);
            commentText.setVisibility(View.GONE);
        }
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateText(LocalDate date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear()) + "-" +
                (date.getMonth()) + "-" + date.getDayOfMonth();
    }

    /**
     * Handles navigation to to edit habit event
     */
    public void navigateToEditHabitEvent() {
        Intent i = new Intent(this, EditHabitEventActivity.class);
        i.putExtra("HabitEvent", habitEvent);
        startActivity(i);
    }
}