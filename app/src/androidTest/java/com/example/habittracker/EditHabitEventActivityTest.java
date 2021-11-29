package com.example.habittracker;

import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditHabitEventActivityTest {
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
     * check navigation to edit new habit event activity
     */
    @Test
    public void checkNavigateToEditNewHabitEventActivity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        View habitEvent = solo.getView((R.id.navigation_habit_events));
        solo.clickOnView(habitEvent);
        solo.clickOnText("Test for Habit Event");
        solo.assertCurrentActivity("Wrong Activity", ViewHabitEventActivity.class);

        solo.clickOnButton("Edit Habit Event");
        solo.assertCurrentActivity("Wrong Activity", EditHabitEventActivity.class);

        solo.goBack();
        solo.goBack();

        View profile = solo.getView(R.id.navigation_profile);
        solo.clickOnView(profile);
        // Logout and check to see if we return to LoginActivity
        solo.clickOnButton("Log Out");

        solo.sleep(2000);
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * Test editing a habit event
     */
    @Test
    public void checkEditHabitEvent() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        View habitEvent = solo.getView((R.id.navigation_habit_events));
        solo.clickOnView(habitEvent);
        solo.clickOnText("Test for Habit Event");
        solo.assertCurrentActivity("Wrong Activity", ViewHabitEventActivity.class);

        solo.clickOnButton("Edit Habit Event");
        solo.assertCurrentActivity("Wrong Activity", EditHabitEventActivity.class);

        solo.enterText((EditText) solo.getView(R.id.completedDate_editText), "2021-11-16");
        solo.clickOnButton("SAVE CHANGES");
        Assert.assertTrue(solo.searchText("2021-11-16"));

        solo.goBack();

        View profile = solo.getView(R.id.navigation_profile);
        solo.clickOnView(profile);
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
