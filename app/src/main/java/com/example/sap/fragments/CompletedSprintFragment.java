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
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.example.sap.R;
import com.example.sap.adapters.ActiveSprintAdapter;
import com.example.sap.adapters.CompletedSprintAdapter;
import com.example.sap.adapters.FutureSprintAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedSprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedSprintFragment extends Fragment {

    private static final String SPRINT_LIST = "sprintList";
    private ArrayList<Sprint> mSprintList;
    RecyclerView rcvCompletedSprint;
    private CompletedSprintAdapter completedSprintAdapter;
    private Handler mHandler;
    private ImageView imvCompletedSprintEmpty;

    public CompletedSprintFragment() {
        // Required empty public constructor
    }


    public static CompletedSprintFragment newInstance(ArrayList<Sprint> sprintList) {
        CompletedSprintFragment fragment = new CompletedSprintFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(SPRINT_LIST, gson.toJson(sprintList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            Bundle args = getArguments();
            Type founderListType = new TypeToken<ArrayList<Sprint>>() {
            }.getType();
            mSprintList = gson.fromJson(args.getString(SPRINT_LIST), founderListType);
        }

        sprintCreateSubscribe();
        sprintUpdateSubscribe();
        sprintDeleteSubscribe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvCompletedSprint = getView().findViewById(R.id.rcvCompletedSprint);
        imvCompletedSprintEmpty = getView().findViewById(R.id.imvCompletedSprintEmpty);
        completedSprintAdapter = new CompletedSprintAdapter(getContext(), mSprintList);
        rcvCompletedSprint.setAdapter(completedSprintAdapter);
        rcvCompletedSprint.setLayoutManager(new LinearLayoutManager(getContext()));
        completedSprintAdapter.setOnItemClickListener(new CompletedSprintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: Open  Sprint Detail Activity
            }
        });
        mHandler.post(() -> {
            completedSprintAdapter.notifyDataSetChanged();
            if (mSprintList.isEmpty()) {
                imvCompletedSprintEmpty.setImageResource(R.drawable.img_empty);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_sprint, container, false);
    }

    private void sprintQuery() {
        if (mSprintList != null) {
            // Get active sprint
            Amplify.API.query(
                    ModelQuery.get(Project.class, getProjectID()),
                    getProjectRes -> {
                        mSprintList.clear();
                        for(Sprint sprint : getProjectRes.getData().getSprints()) {
                            if(!sprint.getIsBacklog()) {
                                if(sprint.getIsCompleted() != null && sprint.getIsCompleted()) {
                                    mSprintList.add(sprint);
                                }
                            }
                        }
                        mHandler.post(() -> {
                            completedSprintAdapter.notifyDataSetChanged();
                        });
                    },
                    error -> Log.e("GetProject", error.toString())
            );
        }
    }

    private void sprintCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Sprint.class),
                onEstablished -> Log.i("OnCreateSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    sprintQuery();
                },
                onFailure -> Log.e("OnCreateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnCreateSprintSubscribe", "Subscription completed")
        );
    }

    private void sprintUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Sprint.class),
                onEstablished -> Log.i("OnUpdateSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    sprintQuery();
                },
                onFailure -> Log.e("OnUpdateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateSprintSubscribe", "Subscription completed")
        );
    }

    private void sprintDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Sprint.class),
                onEstablished -> Log.i("OnDeleteSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    sprintQuery();
                },
                onFailure -> Log.e("OnDeleteSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteSprintSubscribe", "Subscription completed")
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