package com.example.habittracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.habittracker.R;
import com.example.habittracker.models.Follow.Follow;
import com.example.habittracker.models.Follow.FollowList;

public class FollowersAdapter extends ArrayAdapter<Follow> {

    private Context context;
    private FollowList followList;

    public FollowersAdapter(@NonNull Context context, FollowList followList) {
        super(context, 0, followList.getFollowList());
        this.context = context;
        this.followList = followList;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_followers_list, parent, false);
        }

        Follow follow = followList.getFollow(position);

        TextView usernameList = view.findViewById(R.id.followersUsernameList);

        usernameList.setText(follow.getUsername());

        return view;
    }
}
