package com.example.sap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.fragments.BacklogFragment;
import com.example.sap.fragments.DoneFragment;
import com.example.sap.fragments.InProgressFragment;
import com.example.sap.fragments.ToDoFragment;

import java.io.IOException;
import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {
    private int tabNumber;
    private ArrayList<Task> todoTasks;
    private ArrayList<Task> inProgressTasks;
    private ArrayList<Task> doneTasks;
    private ArrayList<Task> backlogTasks;
    private Sprint activeSprint;
    private Sprint backlog;

    public PageAdapter(@NonNull FragmentManager fm, int tabNumber, ArrayList<Task> todoTasks, ArrayList<Task> inProgressTasks, ArrayList<Task> doneTasks, Sprint activeSprint, ArrayList<Task> backlogTasks, Sprint backlog) {
        super(fm);
        this.tabNumber = tabNumber;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.doneTasks = doneTasks;
        this.backlogTasks = backlogTasks;
        this.activeSprint = activeSprint;
        this.backlog = backlog;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return ToDoFragment.newInstance(todoTasks, activeSprint);
            case 1: return InProgressFragment.newInstance(inProgressTasks, activeSprint);
            case 2: return DoneFragment.newInstance(doneTasks, activeSprint);
            case 3: return BacklogFragment.newInstance(backlogTasks, backlog);
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
