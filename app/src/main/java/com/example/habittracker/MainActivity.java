package com.example.habittracker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habittracker.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity, shows the main page which contains the habit list and todos for today
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // assigning ID of the toolbar to a variable
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();

        if (fUser != null) {
            user = new User(fUser.getUid(), fUser.getDisplayName(), "Placeholder");
        } else {
            // Throw error or redirect to login
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_habit_events,
                R.id.navigation_following, R.id.navigation_users, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.navigation_habit_events){
                    MainActivity.this.findViewById(R.id.main_toolbar).setVisibility(View.GONE);
                }
            }
        });


        //HabitListController habitListController = new HabitListController();
        //HabitList habitList = habitListController.loadHabitList(user.getUid());
        // TODO: connect habitList with AddNewHabitActivity so that creating a new habit reloads the list
        // TODO: OR use the habitList directly (return the habit and add it to habit list from here)
        // TODO: i.e. adding new Habit right now does not refresh habit list locally here (add listener?)
    }

}