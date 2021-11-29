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

public class MapsActivityTest {

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
     * check navigation to maps activity
     */
    @Test
    public void checkNavigateToMapsActivity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);


        solo.clickOnText("Test for Habit Event");
        solo.clickOnButton("NEW HABIT EVENT");
        View map = solo.getView(R.id.addLocationBtn);
        solo.clickOnView(map);
        Assert.assertTrue(solo.searchText("Confirm"));

        solo.goBack();
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
     * check navigation to maps activity
     */
    @Test
    public void checkAddNewLocation() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);


        solo.clickOnText("Test for Habit Event");
        solo.clickOnButton("NEW HABIT EVENT");
        View map = solo.getView(R.id.addLocationBtn);
        solo.clickOnView(map);

        solo.sleep(2000);
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity", AddNewHabitEventActivity.class);
        Assert.assertTrue(solo.searchText("USA"));

        View done = solo.getView(R.id.isHabitCompleted);
        solo.clickOnView(done);
        solo.clickOnButton("SAVE CHANGES");
        solo.assertCurrentActivity("Wrong Activity", ViewHabitActivity.class);

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
