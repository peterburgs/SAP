package com.example.sap.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.activities.BacklogEditTaskActivity;
import com.example.sap.activities.CreateTaskActivity;
import com.example.sap.activities.SprintEditTaskActivity;
import com.example.sap.adapters.BacklogAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BacklogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BacklogFragment extends Fragment {

    private static final String TASK_LIST = "taskList";
    private static final String BACKLOG = "backlog";

    private ArrayList<Task> mTaskList;
    private Sprint mBacklog;

    private BacklogAdapter backlogAdapter;
    private Handler mHandler;
    private ImageView imvBacklogEmpty;

    RecyclerView rcvBacklog;
    Button btnCreateTask;

    public BacklogFragment() {
        // Required empty public constructor
    }

    public static BacklogFragment newInstance(ArrayList<Task> taskList, Sprint backlog) {
        BacklogFragment fragment = new BacklogFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(TASK_LIST, gson.toJson(taskList));
        args.putString(BACKLOG, gson.toJson(backlog));
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
            mBacklog = gson.fromJson(args.getString(BACKLOG), Sprint.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backlog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvBacklog = getView().findViewById(R.id.rcvBacklog);
        btnCreateTask = getView().findViewById(R.id.btnCreateTask);
        imvBacklogEmpty = getView().findViewById(R.id.imvBacklogEmpty);

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateTaskActivity.class);
                intent.putExtra("PROJECT_ID", getProjectID());
                startActivity(intent);

            }
        });

        backlogAdapter = new BacklogAdapter(getContext(), mTaskList);
        rcvBacklog.setAdapter(backlogAdapter);
        rcvBacklog.setLayoutManager(new LinearLayoutManager(getContext()));
        backlogAdapter.setOnItemClickListener(new BacklogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), BacklogEditTaskActivity.class);
                intent.putExtra("TASK_ID", mTaskList.get(position).getId());
                startActivity(intent);

            }
        });

        mHandler.post(() -> {
            backlogAdapter.notifyDataSetChanged();
            if (mTaskList.isEmpty()) {
                imvBacklogEmpty.setImageResource(R.drawable.img_empty);
            }
        });

        taskCreateSubscribe();
        taskUpdateSubscribe();
        taskDeleteSubscribe();
    }

    private String getProjectID() {
        String newString;
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("PROJECT_ID");
        }
        return newString;
    }

    private void taskQuery() {
        if (mBacklog != null) {
            // Get tasks of the backlog
            Amplify.API.query(
                    ModelQuery.get(Sprint.class, mBacklog.getId()),
                    getSprintRes -> {
                        mTaskList.clear();
                        mTaskList.addAll(getSprintRes.getData().getTasks());
                        mHandler.post(() -> {
                            backlogAdapter.notifyDataSetChanged();
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