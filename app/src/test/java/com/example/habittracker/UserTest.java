package com.example.habittracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import com.google.firebase.auth.FirebaseAuth;

public class UserTest {
    private FirebaseAuth fAuth;
    private String uid;

//
//    @Before
//    public void login() {
////        FirebaseApp.initializeApp();
//
//        fAuth = FirebaseAuth.getInstance();
//
//        fAuth.signInWithEmailAndPassword(
//                Objects.requireNonNull(System.getenv("FIREBASE_TEST_ACCOUNT_EMAIL"),
//                        "FIREBASE_TEST_ACCOUNT_EMAIL env var cannot be null"),
//                Objects.requireNonNull(System.getenv("FIREBASE_TEST_ACCOUNT_PASSWORD"),
//                        "FIREBASE_TEST_ACCOUNT_PASSWORD env var cannot be null"))
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Log.d("Test", "Successfully logged in");
//                } else fail("Failed to log in");
//            }
//        });
//
//        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
//    }

//    @Test
//    public void userTest() {
//        // since constructor calls loadUserData this test indirectly tests that function as well
//        // so assume it works if this test passes
//        User user = new User(uid);
//        assertTrue(user.isDataLoaded());
//        assertEquals(user.getUsername(), "Test Name");
//        assertEquals(user.getInfo(), "Test info 1");
//    }
//
//    @Test
//    public void updateInfoTest() {
//        User user = new User(uid);
//        user.updateInfo("Test info 2");
//        user.loadUserData();
//        assertEquals(user.getInfo(), "Test info 2");
//    }
}
