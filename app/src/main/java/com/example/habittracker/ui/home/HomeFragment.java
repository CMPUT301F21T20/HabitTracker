package com.example.habittracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.habittracker.R;
import com.example.habittracker.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private ViewPager2 habits_ViewPager2;
    private TabLayout habits_TabLayout;
    private String[] tabs = {"TOTAL", "TODAY", "UNBROKEN"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        habits_ViewPager2 = root.findViewById(R.id.habits_ViewPager2);
        habits_TabLayout = root.findViewById(R.id.habits_TabLayout);

        habits_ViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        habits_ViewPager2.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(habits_TabLayout, habits_ViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs[position]);
            }
        }).attach();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        return root;
    }
}