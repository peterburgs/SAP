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
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.adapters.ActiveSprintAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveSprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveSprintFragment extends Fragment {


    private static final String SPRINT_LIST = "sprintList";
    private ArrayList<Sprint> mSprintList;
    RecyclerView rcvActiveSprint;
    private ActiveSprintAdapter activeSprintAdapter;
    private Handler mHandler;
    private ImageView imvActiveSprintEmpty;


    public ActiveSprintFragment() {
        // Required empty public constructor
    }

    public static ActiveSprintFragment newInstance(ArrayList<Sprint> sprintList) {
        ActiveSprintFragment fragment = new ActiveSprintFragment();
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

        rcvActiveSprint = getView().findViewById(R.id.rcvActiveSprint);
        imvActiveSprintEmpty = getView().findViewById(R.id.imvActiveEmpty);
        activeSprintAdapter = new ActiveSprintAdapter(getContext(), mSprintList);
        rcvActiveSprint.setAdapter(activeSprintAdapter);
        rcvActiveSprint.setLayoutManager(new LinearLayoutManager(getContext()));
        activeSprintAdapter.setOnItemClickListener(new ActiveSprintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: Open  Sprint Detail Activity
            }
        });
        mHandler.post(() -> {
            activeSprintAdapter.notifyDataSetChanged();
            if (mSprintList.isEmpty()) {
                imvActiveSprintEmpty.setImageResource(R.drawable.img_empty);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_sprint, container, false);
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
                                if(sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                    mSprintList.add(sprint);
                                }
                            }
                        }
                        mHandler.post(() -> {
                            activeSprintAdapter.notifyDataSetChanged();
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