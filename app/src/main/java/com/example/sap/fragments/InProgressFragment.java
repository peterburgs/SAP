package com.example.sap.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.activities.SprintEditTaskActivity;
import com.example.sap.adapters.InProgressAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InProgressFragment extends Fragment {

    private static final String TASK_LIST = "taskList";
    private static final String ACTIVE_SPRINT = "activeSprint";
    private static final String ASSIGNEE_LIST = "assigneeList";


    androidx.appcompat.widget.AppCompatSpinner spnInProgressFilter;
    private ArrayList<String> mAssigneeList;
    ArrayAdapter spnInProgressFilterAdapter;
    ArrayList<Task> mTaskByParticipant;


    private ArrayList<Task> mTaskList;
    private ArrayList<Sprint> mActiveSprint;

    RecyclerView rcvInProgress;
    private InProgressAdapter inProgressAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    private ImageView imvInProgressEmpty;


    //
    public InProgressFragment() {
        // Required empty public constructor
    }

    public static InProgressFragment newInstance(ArrayList<Task> taskList, ArrayList<Sprint> activeSprint, ArrayList<String> assigneeList) {
        InProgressFragment fragment = new InProgressFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(TASK_LIST, gson.toJson(taskList));
        args.putString(ACTIVE_SPRINT, gson.toJson(activeSprint));
        args.putString(ASSIGNEE_LIST, gson.toJson(assigneeList));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            Bundle args = getArguments();
            Type taskListType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            Type activeSprintType = new TypeToken<ArrayList<Sprint>>() {
            }.getType();
            Type assigneeListType = new TypeToken<ArrayList<String>>() {
            }.getType();
            mTaskList = gson.fromJson(args.getString(TASK_LIST), taskListType);
            mActiveSprint = gson.fromJson(args.getString(ACTIVE_SPRINT), activeSprintType);
            mAssigneeList = gson.fromJson(args.getString(ASSIGNEE_LIST), assigneeListType);

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvInProgress = getView().findViewById(R.id.rcvInProgress);
        tvDayRemaining = getView().findViewById(R.id.tvInProgressDayRemaining);
        imvInProgressEmpty = getView().findViewById(R.id.imvInProgressEmpty);
        spnInProgressFilter = getView().findViewById(R.id.spnInProgressFilter);


        spnInProgressFilterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, mAssigneeList);
        spnInProgressFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnInProgressFilter.setAdapter(spnInProgressFilterAdapter);
        mTaskByParticipant = new ArrayList<>();

        spnInProgressFilter.setSelection(mAssigneeList.indexOf("All"));
        spnInProgressFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTaskByParticipant.clear();
                if (mAssigneeList.get(position).equals("All")) {

                    mTaskByParticipant.addAll(mTaskList);
                    inProgressAdapter.notifyDataSetChanged();
                } else {
                    for (Task t : mTaskList) {
                        if (t.getAssignee().getUsername().equals(mAssigneeList.get(position))) {
                            mTaskByParticipant.add(t);
                            inProgressAdapter.notifyDataSetChanged();
                        }

                    }
                    if (mTaskByParticipant.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Cannot find task by this participant!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                spnInProgressFilter.setSelection(mAssigneeList.indexOf("All"));
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inProgressAdapter = new InProgressAdapter(getContext(), mTaskByParticipant);

        rcvInProgress.setAdapter(inProgressAdapter);
        rcvInProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        inProgressAdapter.setOnItemClickListener(new InProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), SprintEditTaskActivity.class);
                intent.putExtra("TASK_ID", mTaskByParticipant.get(position).getId());
                startActivity(intent);
            }
        });

        mHandler.post(() -> {
            if (!mActiveSprint.isEmpty()) {
                getDayRemaining();
                if (mTaskByParticipant.isEmpty()) {
                    imvInProgressEmpty.setVisibility(View.VISIBLE);
                    imvInProgressEmpty.setImageResource(R.drawable.img_empty);
                } else {
                    imvInProgressEmpty.setVisibility(View.GONE);
                }
            } else {
                imvInProgressEmpty.setVisibility(View.VISIBLE);
                imvInProgressEmpty.setImageResource(R.drawable.img_empty);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress, container, false);
    }

    private void getDayRemaining() {
        long diffInMillies = mActiveSprint.get(0).getEndDate().toDate().getTime() - System.currentTimeMillis();
        long diff = 0;
        if (diffInMillies >= 0) {
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        tvDayRemaining.setText(diff + " remaining days");
    }
}