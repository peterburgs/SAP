package com.example.sap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sap.fragments.BacklogFragment;
import com.example.sap.fragments.DoneFragment;
import com.example.sap.fragments.InProgressFragment;
import com.example.sap.fragments.ToDoFragment;

public class PageAdapter extends FragmentPagerAdapter {
    private int tabNumber;

    public PageAdapter(@NonNull FragmentManager fm, int tabNumber) {
        super(fm);
        this.tabNumber = tabNumber;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ToDoFragment();
            case 1: return new InProgressFragment();
            case 2: return new DoneFragment();
            case 3: return new BacklogFragment();
            default: return null;
        }
    }
    @Override
    public int getCount() {
        return tabNumber;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
