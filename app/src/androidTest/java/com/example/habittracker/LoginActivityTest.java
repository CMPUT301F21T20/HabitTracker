package com.example.habittracker;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    private Solo solo;

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
    }

    /**
     * check if the current activity is the main activity
     */
    @Test
    public void checkInLoginActivity() {
        // Asserts that the current activity is the LoginActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * check if the link to register activity works
     */
    @Test
    public void checkRegisterLink() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.clickOnText("Create Account");
        solo.assertCurrentActivity("Wrong Activity", RegisterActivity.class);
    }

    /**
     * check if the login button works
     */
    @Test
    public void checkLoginButton() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.email), "test_acc@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "123456");
        solo.clickOnButton("SIGN IN");

        // We should be in main activity after signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Logout and check to see if we return to LoginActivity
        solo.clickOnButton("Log Out");
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
