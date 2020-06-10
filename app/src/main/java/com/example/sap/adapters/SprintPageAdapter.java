package com.example.sap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.fragments.ActiveSprintFragment;
import com.example.sap.fragments.BacklogFragment;
import com.example.sap.fragments.CompletedSprintFragment;
import com.example.sap.fragments.DoneFragment;
import com.example.sap.fragments.FutureSprintFragment;
import com.example.sap.fragments.InProgressFragment;
import com.example.sap.fragments.ToDoFragment;

import java.util.ArrayList;

public class SprintPageAdapter extends FragmentStatePagerAdapter {

    private int tabNumber;
    private ArrayList<Sprint> activeSprints;
    private ArrayList<Sprint> futureSprints;
    private ArrayList<Sprint> completedSprints;

    public SprintPageAdapter(@NonNull FragmentManager fm, int tabNumber, ArrayList<Sprint> activeSprints, ArrayList<Sprint> futureSprints, ArrayList<Sprint> completedSprints) {
        super(fm);
        this.tabNumber = tabNumber;
        this.activeSprints = activeSprints;
        this.futureSprints = futureSprints;
        this.completedSprints = completedSprints;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ActiveSprintFragment.newInstance(activeSprints);
            case 1:
                return FutureSprintFragment.newInstance(futureSprints);
            case 2:
                return CompletedSprintFragment.newInstance(completedSprints);
            default:
                return null;
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
