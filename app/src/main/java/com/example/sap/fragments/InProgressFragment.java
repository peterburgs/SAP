package com.example.sap.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.activities.SprintEditTaskActivity;
import com.example.sap.adapters.InProgressAdapter;
import com.example.sap.adapters.ToDoAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InProgressFragment extends Fragment {

    private static final String TASK_LIST = "taskList";
    private static final String ACTIVE_SPRINT = "activeSprint";

    private ArrayList<Task> mTaskList;
    private Sprint mActiveSprint;


    RecyclerView rcvInProgress;
    private InProgressAdapter inProgressAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    private ImageView imvInProgressEmpty;

    //
    public InProgressFragment() {
        // Required empty public constructor
    }

    public static InProgressFragment newInstance(ArrayList<Task> taskList, Sprint activeSprint) {
        InProgressFragment fragment = new InProgressFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(TASK_LIST, gson.toJson(taskList));
        args.putString(ACTIVE_SPRINT, gson.toJson(activeSprint));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            Bundle args = getArguments();
            Type founderListType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            mTaskList = gson.fromJson(args.getString(TASK_LIST), founderListType);
            mActiveSprint = gson.fromJson(args.getString(ACTIVE_SPRINT), Sprint.class);
        }

        taskCreateSubscribe();
        taskUpdateSubscribe();
        taskDeleteSubscribe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvInProgress = getView().findViewById(R.id.rcvInProgress);
        tvDayRemaining = getView().findViewById(R.id.tvInProgressDayRemaining);
        imvInProgressEmpty = getView().findViewById(R.id.imvInProgressEmpty);

        inProgressAdapter = new InProgressAdapter(getContext(), mTaskList);

        rcvInProgress.setAdapter(inProgressAdapter);
        rcvInProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        inProgressAdapter.setOnItemClickListener(new InProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), SprintEditTaskActivity.class);
                intent.putExtra("TASK_ID", mTaskList.get(position).getId());
                startActivity(intent);
            }
        });

        mHandler.post(() -> {
            inProgressAdapter.notifyDataSetChanged();
            if (mTaskList.isEmpty()) {
                imvInProgressEmpty.setImageResource(R.drawable.img_empty);
            } else {
                try {
                    if (mActiveSprint != null) {
                        getDayRemaining(mActiveSprint);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress, container, false);
    }

    private void getDayRemaining(Sprint activeSprint) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date firstDate = sdf.parse(LocalDate.now().toString());
        String end = activeSprint.getEndDate().format();
        Date secondDate = sdf.parse(end.substring(0, end.length() - 1));

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        tvDayRemaining.setText(String.valueOf(diff) + " remaining days");
    }

    private void taskQuery() {
        if (mActiveSprint != null) {
            // Get tasks of the activated sprint
            Amplify.API.query(
                    ModelQuery.get(Sprint.class, mActiveSprint.getId()),
                    getSprintRes -> {
                        mTaskList.clear();
                        for (Task task : getSprintRes.getData().getTasks()) {
                            if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                                mTaskList.add(task);
                            }
                        }
                        mHandler.post(() -> {
                            inProgressAdapter.notifyDataSetChanged();
                        });
                    },
                    error -> Log.e("GetSprintError", error.toString())
            );
        }
    }

    private void taskCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Task.class),
                onEstablished -> Log.i("OnCreateTaskSubscribe", "Subscription established"),
                onCreated -> {
                    taskQuery();
                },
                onFailure -> Log.e("OnCreateTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnCreateTaskSubscribe", "Subscription completed")
        );
    }

    private void taskUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Task.class),
                onEstablished -> Log.i("OnUpdateTaskSubscribe", "Subscription established"),
                onUpdated -> {
                    taskQuery();
                },
                onFailure -> Log.e("OnUpdateTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateTaskSubscribe", "Subscription completed")
        );
    }

    private void taskDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Task.class),
                onEstablished -> Log.i("OnDeleteTaskSubscribe", "Subscription established"),
                onDeleted -> {
                    taskQuery();
                },
                onFailure -> Log.e("OnDeleteTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteTaskSubscribe", "Subscription completed")
        );
    }
}