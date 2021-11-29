package com.example.habittracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.adapters.UserHabitListAdapter;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;
import com.example.habittracker.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userProfile_back_icon;
    private User user;
    private TextView name;
    private HabitList habitList;
    private HabitList showHabitList;
    private ArrayAdapter<Habit> habitListAdapter;
    private ListView habitsListView;
    private FirebaseFirestore db;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        user = (User) intent.getSerializableExtra("User");
        userProfile_back_icon = findViewById(R.id.viewHabit_back_icon);
        name = findViewById(R.id.userProfileName);
        habitsListView = findViewById(R.id.userHabits_listview);
        name.setText(user.getUsername());
        habitList = new HabitList();
        showHabitList = new HabitList();
        habitListAdapter = new UserHabitListAdapter(this, showHabitList);
        habitsListView.setAdapter(habitListAdapter);

        db.collection("Habits").document(user.getUid()).addSnapshotListener((docSnapshot, e) -> {
            HabitListController.convertToHabitList(docSnapshot, habitList);
            for (int i = 0; i < habitList.getCount(); i++) {
                Habit habit = habitList.getHabit(i);
                if (habit.getCanShare() == true){
                    showHabitList.addHabit(habit);
                }
            }
            habitListAdapter.notifyDataSetChanged();
        });

        habitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                Habit habit = (Habit) showHabitList.getHabit(position);
                openViewHabitActivity(habit);

            }
        });

        userProfile_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userProfile_back_icon.setAlpha(0.5f);
                onSupportNavigateUp();
            }
        });
    }

    public void openViewHabitActivity(Habit habit) {
        Intent i = new Intent(this, ViewHabitActivity.class);
        i.putExtra("Habit", habit);
        i.putExtra("pActivity", "UserProfile");
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
