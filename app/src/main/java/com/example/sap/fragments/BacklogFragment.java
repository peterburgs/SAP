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
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.adapters.BacklogAdapter;
import com.example.sap.adapters.ToDoAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BacklogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BacklogFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<Task> taskList;
    private BacklogAdapter backlogAdapter;
    private Handler mHandler;

    RecyclerView rcvBacklog;

    public BacklogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BacklogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BacklogFragment newInstance(String param1, String param2) {
        BacklogFragment fragment = new BacklogFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backlog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());

        rcvBacklog = getView().findViewById(R.id.rcvBacklog);
        backlogAdapter = new BacklogAdapter(getContext(), taskList);

        rcvBacklog.setAdapter(backlogAdapter);
        rcvBacklog.setLayoutManager(new LinearLayoutManager(getContext()));
        backlogAdapter.setOnItemClickListener(new BacklogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: handle Nav to EditTask
                Toast.makeText(getContext(), "Task Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        backlogTaskQuery();
    }

    private void backlogTaskQuery() {
        if (getProjectID() != null) {
            // Get project by id
            Amplify.API.query(
                    ModelQuery.get(Project.class, getProjectID()),
                    response -> {
                        taskList.clear();
                        if (response.getData() != null) {
                            Sprint backlog = null;
                            // Get backlog
                            for (Sprint sprint : response.getData().getSprints()) {
                                if (sprint.getIsBacklog()) {
                                    backlog = sprint;
                                }
                            }

                            if (backlog != null) {
                                // Get tasks of the backlog
                                Amplify.API.query(
                                        ModelQuery.get(Sprint.class, backlog.getId()),
                                        getSprintRes -> {
                                            taskList.clear();
                                            for (Task task : getSprintRes.getData().getTasks()) {
                                                taskList.addAll(getSprintRes.getData().getTasks());
                                            }
                                            mHandler.post(() -> backlogAdapter.notifyDataSetChanged());
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