package com.example.habittracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.R;
import com.example.habittracker.UserProfileActivity;
import com.example.habittracker.models.User;
import com.example.habittracker.models.UsersList;

public class UsersListAdapter extends ArrayAdapter<User> {
    private Context context;
    private UsersList usersList;

    public UsersListAdapter(@NonNull Context context, UsersList usersList) {
        super(context, 0, usersList.getUsersList());
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_user_list, parent, false);
        }

        User user = usersList.getUser(position);

        TextView usernameList = view.findViewById(R.id.usernameList);
        //Button viewUserButton = view.findViewById(R.id.userViewButton);
        //Button deleteHabitButton = view.findViewById(R.id.habitListDeleteButton);

        usernameList.setText(user.getUsername());

       //viewUserButton.setOnClickListener(v -> openViewUserActivity(user));

        /*deleteHabitButton.setOnClickListener(v -> {
            HabitListController controller = HabitListController.getInstance();
            controller.deleteHabit(habit);
        });*/

        return view;
    }

    public void openViewUserActivity(User user) {
        Intent i = new Intent(context, UserProfileActivity.class);
        i.putExtra("User", user);
        context.startActivity(i);
    }
}
