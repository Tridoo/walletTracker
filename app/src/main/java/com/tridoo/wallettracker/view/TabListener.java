package com.tridoo.wallettracker.view;

import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;

public class TabListener implements TabLayout.OnTabSelectedListener{

    private final ViewPager2 viewPager2;

    public TabListener(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
