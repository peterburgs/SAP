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
import com.example.sap.adapters.ToDoAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
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
 * Use the {@link ToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {

    private static final String TASK_LIST = "taskList";
    private static final String ACTIVE_SPRINT = "activeSprint";

    private ArrayList<Task> mTaskList;
    private Sprint mActiveSprint;

    RecyclerView rcvToDo;
    private ToDoAdapter toDoAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    private ImageView imvTodoEmpty;

    //
    public ToDoFragment() {
        // Required empty public constructor
    }

    public static ToDoFragment newInstance(ArrayList<Task> taskList, Sprint activeSprint) {
        ToDoFragment fragment = new ToDoFragment();
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
        sprintUpdateSubscribe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvToDo = getView().findViewById(R.id.rcvToDo);
        tvDayRemaining = getView().findViewById(R.id.tvTodoDayRemaining);
        imvTodoEmpty = getView().findViewById(R.id.imvTodoEmpty);

        toDoAdapter = new ToDoAdapter(getContext(), mTaskList);

        rcvToDo.setAdapter(toDoAdapter);
        rcvToDo.setLayoutManager(new LinearLayoutManager(getContext()));
        toDoAdapter.setOnItemClickListener(new ToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), SprintEditTaskActivity.class);
                intent.putExtra("TASK_ID", mTaskList.get(position).getId());
                startActivity(intent);
            }
        });

        mHandler.post(() -> {
            toDoAdapter.notifyDataSetChanged();
            if (mActiveSprint != null) {
                try {
                    getDayRemaining(mActiveSprint);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (mTaskList.isEmpty()) {
                    imvTodoEmpty.setVisibility(View.VISIBLE);
                    imvTodoEmpty.setImageResource(R.drawable.img_empty);
                } else {
                    imvTodoEmpty.setVisibility(View.GONE);
                }
            } else {
                imvTodoEmpty.setVisibility(View.VISIBLE);
                imvTodoEmpty.setImageResource(R.drawable.img_empty);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false);
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

    private void query() {
        mTaskList.clear();
        if (mActiveSprint != null) {
            // Get tasks of the activated sprint
            Amplify.API.query(
                    ModelQuery.get(Sprint.class, mActiveSprint.getId()),
                    getSprintRes -> {
                        for (Task task : getSprintRes.getData().getTasks()) {
                            if (task.getStatus().equals(TaskStatus.TODO)) {
                                mTaskList.add(task);
                            }
                        }
                        mHandler.post(() -> {
                            toDoAdapter.notifyDataSetChanged();
                            if (mActiveSprint != null) {
                                try {
                                    getDayRemaining(mActiveSprint);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (mTaskList.isEmpty()) {
                                    imvTodoEmpty.setVisibility(View.VISIBLE);
                                    imvTodoEmpty.setImageResource(R.drawable.img_empty);
                                } else {
                                    imvTodoEmpty.setVisibility(View.GONE);
                                }
                            } else {
                                imvTodoEmpty.setVisibility(View.VISIBLE);
                                imvTodoEmpty.setImageResource(R.drawable.img_empty);
                            }
                        });
                    },
                    error -> Log.e("GetSprintError", error.toString())
            );
        } else {
            mHandler.post(() -> {
                toDoAdapter.notifyDataSetChanged();
                if (mActiveSprint != null) {
                    try {
                        getDayRemaining(mActiveSprint);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (mTaskList.isEmpty()) {
                        imvTodoEmpty.setVisibility(View.VISIBLE);
                        imvTodoEmpty.setImageResource(R.drawable.img_empty);
                    } else {
                        imvTodoEmpty.setVisibility(View.GONE);
                    }
                } else {
                    imvTodoEmpty.setVisibility(View.VISIBLE);
                    imvTodoEmpty.setImageResource(R.drawable.img_empty);
                }
            });
        }
    }

    private void taskCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Task.class),
                onEstablished -> Log.i("OnCreateTaskSubscribe", "Subscription established"),
                onCreated -> {
                    query();
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
                    query();
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
                    query();
                },
                onFailure -> Log.e("OnDeleteTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteTaskSubscribe", "Subscription completed")
        );
    }

    private void sprintUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Sprint.class),
                onEstablished -> Log.i("OnUpdateSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    mActiveSprint = null;
                    // Get project by id
                    Amplify.API.query(
                            ModelQuery.get(Project.class, getProjectID()),
                            getProjectRes -> {
                                for (Sprint sprint : getProjectRes.getData().getSprints()) {
                                    if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                        mActiveSprint = sprint;
                                    }
                                }
                                query();
                            },
                            error -> {
                                Log.e("Error", error.toString());
                            }
                    );
                },
                onFailure -> Log.e("OnUpdateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateSprintSubscribe", "Subscription completed")
        );
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

}