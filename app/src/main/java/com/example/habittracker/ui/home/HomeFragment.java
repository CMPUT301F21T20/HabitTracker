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
import com.example.habittracker.adapters.HabitsViewPagerAdapter;
import com.example.habittracker.mediator.TabLayoutMediators;
import com.google.android.material.tabs.TabLayout;

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
        HabitsViewPagerAdapter habitsViewPagerAdapter = new HabitsViewPagerAdapter(this);
        habits_ViewPager2.setAdapter(habitsViewPagerAdapter);
        habits_ViewPager2.setPageTransformer(new ScaleInTransformer());
        new TabLayoutMediators(habits_TabLayout, habits_ViewPager2, true,new TabLayoutMediators.TabConfigurationStrategy() {
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


    /**
     * realize scaled pager scroll
     */
    private static class ScaleInTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1 + position);
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        habits_TabLayout.setVisibility(View.GONE);
        habits_ViewPager2.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        habits_TabLayout.setVisibility(View.VISIBLE);
        habits_ViewPager2.setVisibility(View.VISIBLE);
    }
}