package com.example.habittracker;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;

public class MainActivityTest {
    private Solo solo;

    /**
     * Signs into to the test user's account
     */
    public void signIn() {
        solo.enterText((EditText) solo.getView(R.id.email), "test_acc@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "123456");
        solo.clickOnButton("SIGN IN");
    }

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // First sign in
        signIn();
    }

    /**
     * check if the current activity is the main activity
     */
    @Test
    public void checkNavigateToMainActivity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        View profile = solo.getView(R.id.navigation_profile);
        solo.clickOnView(profile);
        // Logout and check to see if we return to LoginActivity
        solo.clickOnButton("Log Out");

        solo.sleep(2000);
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * check navigation to add new habit activity
     */
    @Test
    public void checkNavigationBar() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Navigate to habit event page and check
        View habitEvent = solo.getView(R.id.navigation_habit_events);
        solo.clickOnView(habitEvent);
        Assert.assertTrue(solo.searchText("Habit Events"));

        // Navigate to following page and check
        View following = solo.getView(R.id.navigation_request);
        solo.clickOnView(following);
        Assert.assertTrue(solo.searchText("Following"));

        // Navigate to user page and check
        View users = solo.getView(R.id.navigation_users);
        solo.clickOnView(users);
        Assert.assertTrue(solo.searchText("Users"));

        // Navigate to profile page and check
        View profile = solo.getView(R.id.navigation_profile);
        solo.clickOnView(profile);
        Assert.assertTrue(solo.searchText("Profile"));

        // Logout and check to see if we return to LoginActivity
        solo.clickOnButton("Log Out");

        solo.sleep(2000);
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }


    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
