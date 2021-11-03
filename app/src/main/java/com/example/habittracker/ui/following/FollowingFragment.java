package com.example.habittracker.ui.following;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habittracker.R;


public class FollowingFragment extends Fragment {

    private FollowingViewModel followingViewModel;
    private ListView followingList;
    private Context thiscontext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        thiscontext = container.getContext();

        followingViewModel =
                new ViewModelProvider(this).get(FollowingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_following_list, container, false);
        followingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                return;
            }
        });

        followingList = root.findViewById(R.id.following_list);
        followingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                String following = (String) followingList.getItemAtPosition(position);
                openFollowFragment(following);
            }
        });
        return root;

    }

    public void openFollowFragment(String following) {
        //TODO: following is used to make sure which user we are looking for later
        Intent intent = new Intent(thiscontext, FollowFragment.class);
        startActivity(intent);
    }
}
