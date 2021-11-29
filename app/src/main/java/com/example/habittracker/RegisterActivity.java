package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is for register a new user, record user credentials to firestore
 */
public class RegisterActivity extends AppCompatActivity {
    EditText FullName, Email, Password;
    Button RegisterButton;
    TextView LoginPageBtn;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FullName = findViewById(R.id.fullName);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        RegisterButton = findViewById(R.id.regButton);
        LoginPageBtn = findViewById(R.id.loginPageBtn);

        fAuth = FirebaseAuth.getInstance();

        RegisterButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Get user credentials and send to firestore
             */
            @Override
            public void onClick(View view) {
                String fullName = FullName.getText().toString().trim();
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Password.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    Password.setError("Password must be at least 6 characters");
                }
                // Add Progress Bar

                // Register the user
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("username", fullName);
                            newUser.put("info", "");

                            newUser.put("following", null);
                            newUser.put("followers", null);
                            try {
                                // create user document
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String uid = fAuth.getCurrentUser().getUid();
                                db.collection("Users").document(uid)
                                        .set(newUser)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                                        .addOnFailureListener(e -> {
                                            // TODO: throw error - user auth successful but user doc creation failed
                                            Log.w("Firestore", "Error writing document", e);
                                        });
                                Map<String, Object> mapping = new HashMap<>();

                                // create request document
                                mapping.put("outgoing", new HashMap<>());
                                mapping.put("incoming", new HashMap<>());
                                db.collection("Requests").document(uid)
                                        .set(mapping)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                                        .addOnFailureListener(e -> {
                                            // TODO: throw error - user auth successful but user doc creation failed
                                            Log.w("Firestore", "Error writing document", e);
                                        });
                            } catch (Exception error) {
                                // TODO: retry?
                                Log.w("Firestore", error);
                            }


                            Toast.makeText(RegisterActivity.this, "Account Registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error occurred!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        LoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}