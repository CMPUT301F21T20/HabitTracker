package com.example.habittracker;

import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habittracker.ui.users.UsersFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UserProfileActivityTest {

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
     * check navigation to view habit activity
     */
    @Test
    public void checkNavigateToUSerProfileActivity() {
        // Wait for activity to be pulled up
        solo.sleep(3000);

        // Check if we are in Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Navigate to Add Activity and check

        View users = solo.getView(R.id.navigation_users);
        solo.clickOnView(users);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnText("testcase");
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);

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
