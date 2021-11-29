package com.example.habittracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habittracker.fragments.HabitsTodayFragment;
import com.example.habittracker.fragments.HabitsTotalFragment;

public class HabitsViewPagerAdapter extends FragmentStateAdapter {

    public HabitsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HabitsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HabitsViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

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

    @Override
    public int getItemCount() {
        return 2;
    }
}
