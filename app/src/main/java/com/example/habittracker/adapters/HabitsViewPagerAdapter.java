package com.example.habittracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habittracker.fragments.HabitsTodayFragment;
import com.example.habittracker.fragments.HabitsTotalFragment;

/**
 *  This class handles the top navigator on the home page for the sections of our habit
 */
public class HabitsViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Contructor for HabitsViewPagerAdapter
     */
    public HabitsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Contructor for HabitsViewPagerAdapter
     */
    public HabitsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Contructor for HabitsViewPagerAdapter
     */
    public HabitsViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * Handles selection of top header on home page
     * @param position the index of the selected element
     * @return the fragment to display
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HabitsTotalFragment();
            case 1:
                return new HabitsTodayFragment();
        }
        return null;
    }

    /**
     * Gets number of items in state adpater
     * @return the number of items
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
