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
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.activities.EditTaskActivity;
import com.example.sap.adapters.InProgressAdapter;
import com.example.sap.adapters.ToDoAdapter;

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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView rcvInProgress;

    private ArrayList<Task> taskList;
    private InProgressAdapter inProgressAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    private ImageView imvInProgressEmpty;

    //
    public InProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InProgressFragment.
     */
    public static InProgressFragment newInstance(String param1, String param2) {
        InProgressFragment fragment = new InProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());

        rcvInProgress = getView().findViewById(R.id.rcvInProgress);
        inProgressAdapter = new InProgressAdapter(getContext(), taskList);
        tvDayRemaining = getView().findViewById(R.id.tvInProgressDayRemaining);
        imvInProgressEmpty = getView().findViewById(R.id.imvInProgressEmpty);

        rcvInProgress.setAdapter(inProgressAdapter);
        rcvInProgress.setLayoutManager(new LinearLayoutManager(getContext()));

        inProgressAdapter.setOnItemClickListener(new InProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), EditTaskActivity.class);
                intent.putExtra("TASK_ID", taskList.get(position).getId());
                startActivity(intent);
            }
        });

        inProgressTasksQuery();
        taskUpdateSubscribe();
        taskDeleteSubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress, container, false);
    }

    private void inProgressTasksQuery() {
        if (getProjectID() != null) {
            // Get project by id
            Amplify.API.query(
                    ModelQuery.get(Project.class, getProjectID()),
                    response -> {
                        taskList.clear();
                        if (response.getData() != null) {
                            Sprint activatedSprint = null;
                            // Get activated sprint
                            for (Sprint sprint : response.getData().getSprints()) {
                                if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                    activatedSprint = sprint;
                                }
                            }

                            if (activatedSprint != null) {
                                // Get tasks of the sprint
                                Sprint finalActivatedSprint = activatedSprint;
                                Amplify.API.query(
                                        ModelQuery.get(Sprint.class, activatedSprint.getId()),
                                        getSprintRes -> {
                                            taskList.clear();
                                            for (Task task : getSprintRes.getData().getTasks()) {
                                                if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                                                    taskList.add(task);
                                                }
                                            }
                                            mHandler.post(() -> {
                                                inProgressAdapter.notifyDataSetChanged();
                                                if (taskList.isEmpty()) {
                                                    imvInProgressEmpty.setImageResource(R.drawable.img_empty);
                                                } else {
                                                    try {
                                                        getDayRemaining(finalActivatedSprint);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        },
                                        error -> Log.e("GetSprintError", error.toString())
                                );
                            }
                        }
                    },
                    error -> {
                        Log.e("GetProjectError", error.toString());
                    }
            );
        }
    }

    private void getDayRemaining(Sprint activatedSprint) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date firstDate = sdf.parse(LocalDate.now().toString());
        String end = activatedSprint.getEndDate().format();
        Date secondDate = sdf.parse(end.substring(0, end.length() - 1));

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        tvDayRemaining.setText(String.valueOf(diff) + " remaining days");
    }

    private void taskUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Task.class),
                onEstablished -> Log.i("OnUpdateTaskSubscribe", "Subscription established"),
                onUpdated -> {
                    inProgressTasksQuery();
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
                    inProgressTasksQuery();
                },
                onFailure -> Log.e("OnDeleteTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteTaskSubscribe", "Subscription completed")
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