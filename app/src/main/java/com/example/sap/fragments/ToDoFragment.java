package com.example.sap.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.activities.CreateProjectActivity;
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
 * Use the {@link ToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView rcvToDo;

    private ArrayList<Task> taskList;
    private ToDoAdapter toDoAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    //
    public ToDoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoFragment newInstance(String param1, String param2) {
        ToDoFragment fragment = new ToDoFragment();
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

        rcvToDo = getView().findViewById(R.id.rcvToDo);
        tvDayRemaining = getView().findViewById(R.id.tvTodoDayRemaining);

        toDoAdapter = new ToDoAdapter(getContext(), taskList);


        rcvToDo.setAdapter(toDoAdapter);
        rcvToDo.setLayoutManager(new LinearLayoutManager(getContext()));
        toDoAdapter.setOnItemClickListener(new ToDoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: handle Nav to EditTask
                Toast.makeText(getContext(), "Task Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        todoTasksQuery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    private void todoTasksQuery() {
        if (getProjectID() != null) {
            // Get project by id
            Amplify.API.query(
                    ModelQuery.get(Project.class, getProjectID()),
                    response -> {
                        taskList.clear();
                        if(response.getData() != null) {
                            Sprint activatedSprint = null;
                            // Get activated sprint
                            for (Sprint sprint : response.getData().getSprints()) {
                                if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                    activatedSprint = sprint;
                                }
                            }

                            if(activatedSprint != null) {
                                // Get tasks of the sprint
                                Sprint finalActivatedSprint = activatedSprint;
                                Amplify.API.query(
                                        ModelQuery.get(Sprint.class, activatedSprint.getId()),
                                        getSprintRes -> {
                                            taskList.clear();
                                            for(Task task : getSprintRes.getData().getTasks()) {
                                                if(task.getStatus().equals(TaskStatus.TODO)) {
                                                    taskList.add(task);
                                                }
                                            }
                                            mHandler.post(() -> {
                                                try {
                                                    getDayRemaining(finalActivatedSprint);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                toDoAdapter.notifyDataSetChanged();
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