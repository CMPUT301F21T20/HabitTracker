package com.example.habittracker;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habittracker.activities.LoginActivity;
import com.example.habittracker.activities.MainActivity;
import com.example.habittracker.activities.RegisterActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

public class RegisterActivityTest {
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
     * check if navigate to register activity works
     */
    @Test
    public void checkNavigateToRegister() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.clickOnText("Create Account");
        solo.assertCurrentActivity("Wrong Activity", RegisterActivity.class);
    }

    @Test
    public void CheckRegisterButton() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.clickOnText("Create Account");
        solo.assertCurrentActivity("Wrong Activity", RegisterActivity.class);

        // create a unique email for testing purposes
        String uniqueEmail = UUID.randomUUID().toString() + "@gmail.com";

        solo.enterText((EditText) solo.getView(R.id.fullName), "Test Account");
        solo.enterText((EditText) solo.getView(R.id.email), uniqueEmail);
        solo.enterText((EditText) solo.getView(R.id.password), "123456");

        solo.clickOnButton("Sign Up");

        solo.sleep(5000);

        // Should be in Main Activity after signing up
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

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
