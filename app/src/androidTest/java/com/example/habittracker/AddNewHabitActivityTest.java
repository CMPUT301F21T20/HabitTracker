package com.example.habittracker;

import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habittracker.activities.AddNewHabitActivity;
import com.example.habittracker.activities.LoginActivity;
import com.example.habittracker.activities.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AddNewHabitActivityTest {
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
     * check navigation to add new habit activity
     */
    @Test
    public void checkNavigateToAddHabitActivity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Navigate to Add Activity and check
        View fab = solo.getView(R.id.addHabitButton);
        solo.clickOnView(fab);
        solo.assertCurrentActivity("Wrong Activity", AddNewHabitActivity.class);

        solo.goBack();

        // Logout and check to see if we return to LoginActivity
        solo.clickOnButton("Log Out");

        solo.sleep(2000);
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * Test adding a city
     */
    @Test
    public void checkAddCity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Navigate to Add Activity and check
        View fab = solo.getView(R.id.addHabitButton);
        solo.clickOnView(fab);
        solo.assertCurrentActivity("Wrong Activity", AddNewHabitActivity.class);

        solo.enterText((EditText) solo.getView(R.id.addHabitTitle), "Test Habit");
        solo.enterText((EditText) solo.getView(R.id.addHabitReason), "Test Reason");

        solo.clickOnButton("PICK DATE");
        solo.setDatePicker(0, 2015, 8, 8);
        solo.clickOnButton("OK");

        solo.clickOnButton("ADD");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        ListView habitList = (ListView) solo.getView(R.id.habitsToday_listview);

        // Check to see if habit was added to list
//        Assert.assertEquals(habitList.getAdapter().getCount(), 2);

//        solo.clickOnButton("DEL");

        // Logout and check to see if we return to LoginActivity
//        solo.clickOnButton("Log Out");

        solo.sleep(2000);
//        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
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
