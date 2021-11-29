package com.example.habittracker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.adapters.UserHabitListAdapter;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.controllers.SocialController;
import com.example.habittracker.models.Habit.Habit;
import com.example.habittracker.models.Habit.HabitList;
import com.example.habittracker.models.Request.Request;
import com.example.habittracker.models.User.User;
import com.example.habittracker.activities.follow.FollowersActivity;
import com.example.habittracker.activities.follow.FollowingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Handles functionality of viewing a user profile
 */
public class UserProfileActivity extends AppCompatActivity {
    private ImageView userProfile_back_icon;
    private User user;
    private TextView name;
    private HabitList habitList;
    private HabitList showHabitList;
    private ArrayAdapter<Habit> habitListAdapter;
    private ListView habitsListView;
    private TextView UserProfileNoHabit_textView;
    private FirebaseFirestore db;

    /**
     * Handles functionality when activity is created
     */
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
        boolean follower = intent.getBooleanExtra("follower", false);
        userProfile_back_icon = findViewById(R.id.viewHabit_back_icon);
        name = findViewById(R.id.userProfileName);
        UserProfileNoHabit_textView = findViewById(R.id.UserProfileNoHabit_textView);
        habitsListView = findViewById(R.id.userHabits_listview);
        name.setText(user.getUsername());
        habitList = new HabitList();
        showHabitList = new HabitList();
        habitListAdapter = new UserHabitListAdapter(this, showHabitList);
        habitsListView.setAdapter(habitListAdapter);
        Button followBtn = findViewById(R.id.followButton);
        TextView followers_counter = findViewById(R.id.followers_counter_p);
        TextView following_counter = findViewById(R.id.following_counter_p);

        followers_counter.setText(String.valueOf(user.getFollowers().getFollowList().size()));
        following_counter.setText(String.valueOf(user.getFollowing().getFollowList().size()));

        if (follower) {
            followBtn.setText("Unfollow");
        } else {
            followBtn.setText("Follow");
        }

        followBtn.setOnClickListener(view -> {
            // If following the user, unfollow them otherwise follow them on click
            if (follower) {
                SocialController.getInstance().unfollow(user.getUid());
                Toast.makeText(UserProfileActivity.this, "User unfollowed", Toast.LENGTH_SHORT).show();
                followBtn.setText("Follow");
            } else {
                Request request = new Request(user.getUid(), "Pending", user.getUsername(), new Date());
                SocialController.getInstance().saveRequest("outgoing", request);
                Toast.makeText(UserProfileActivity.this, "Follow Request Sent", Toast.LENGTH_SHORT).show();
            }
        });


//        LinearLayout followerBtn = findViewById(R.id.FollowersButton);
//        followerBtn.setOnClickListener(view -> {
//            // open followers activity
//            Intent i = new Intent(getApplicationContext(), FollowersActivity.class);
//            i.putExtra("User", user);
//            startActivity(i);
//        });
//
//        LinearLayout followingBtn = findViewById(R.id.FollowingButton);
//        followingBtn.setOnClickListener(view -> {
//            // open following activity
//            Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
//            i.putExtra("User", user);
//            startActivity(i);
//        });

        // only load habits if the currently logged in user follows the user whose profile they are
        // attempting to view
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getFollowers().getFollowUids().contains(fUser.getUid())) {
            db.collection("Habits").document(user.getUid()).addSnapshotListener((docSnapshot, e) -> {
                HabitListController.convertToHabitList(docSnapshot, habitList);
                for (int i = 0; i < habitList.getCount(); i++) {
                    UserProfileNoHabit_textView.setVisibility(View.GONE);
                    Habit habit = habitList.getHabit(i);
                    if (habit.getCanShare()) {
                        showHabitList.addHabit(habit);
                    }
                }
                habitListAdapter.notifyDataSetChanged();
            });
        } else {
            UserProfileNoHabit_textView.setText("Must be following the user to view Habits");
            UserProfileNoHabit_textView.setVisibility(View.VISIBLE);
        }

        habitsListView.setOnItemClickListener((arg0, view, position, id) -> {
            Habit habit = (Habit) showHabitList.getHabit(position);
            openViewHabitActivity(habit);
        });

        userProfile_back_icon.setOnClickListener(view -> {
            userProfile_back_icon.setAlpha(0.5f);
            onSupportNavigateUp();
        });
    }

    /**
     * Handles clicking on a habit in the habit list
     * @param habit
     */
    public void openViewHabitActivity(Habit habit) {
        Intent i = new Intent(this, ViewHabitActivity.class);
        i.putExtra("Habit", habit);
        i.putExtra("pActivity", "UserProfile");
        startActivity(i);
    }

    /**
     * Handles clicking on the back button in the header
     * @return false if not working
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
