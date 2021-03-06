package com.example.sap.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.fragments.BacklogFragment;
import com.example.sap.fragments.DoneFragment;
import com.example.sap.fragments.InProgressFragment;
import com.example.sap.fragments.ToDoFragment;

import java.util.ArrayList;

public class PageAdapter extends FragmentStatePagerAdapter {
    private int tabNumber;
    private ArrayList<Task> todoTasks;
    private ArrayList<Task> inProgressTasks;
    private ArrayList<Task> doneTasks;
    private ArrayList<Task> backlogTasks;
    private ArrayList<Sprint> activeSprint;
    private ArrayList<Sprint> backlog;
    private ArrayList<String> assigneeList;

    public PageAdapter(@NonNull FragmentManager fm, int tabNumber, ArrayList<Task> todoTasks, ArrayList<Task> inProgressTasks, ArrayList<Task> doneTasks, ArrayList<Sprint> activeSprint, ArrayList<Task> backlogTasks, ArrayList<Sprint> backlog, ArrayList<String> assigneeList) {
        super(fm);
        this.tabNumber = tabNumber;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.doneTasks = doneTasks;
        this.backlogTasks = backlogTasks;
        this.activeSprint = activeSprint;
        this.backlog = backlog;
        this.assigneeList = assigneeList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ToDoFragment.newInstance(todoTasks, activeSprint, assigneeList);
            case 1:
                return InProgressFragment.newInstance(inProgressTasks, activeSprint, assigneeList);
            case 2:
                return DoneFragment.newInstance(doneTasks, activeSprint, assigneeList);
            case 3:
                return BacklogFragment.newInstance(backlogTasks, backlog);
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
