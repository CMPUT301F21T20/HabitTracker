package com.example.habittracker.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.models.User;

public class ViewUserProfileActivity extends AppCompatActivity {
    private User user;
    private TextView username;


     @Override
    protected void onCreate(Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_user_profile);
         Intent intent = getIntent();
         user = (User) intent.getSerializableExtra("User");

         System.out.println(user);
         TextView name = findViewById(R.id.userProfileName);
         name.setText( user.getUsername());
     }
}
