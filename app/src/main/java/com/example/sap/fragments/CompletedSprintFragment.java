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

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.example.sap.R;
import com.example.sap.activities.ViewCompletedSprintActivity;
import com.example.sap.adapters.CompletedSprintAdapter;
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
                Intent intent = new Intent(getContext(), ViewCompletedSprintActivity.class);
                intent.putExtra("SPRINT_ID", mSprintList.get(position).getId());
                startActivity(intent);
            }
        });
        mHandler.post(() -> {
            if (mSprintList.isEmpty()) {
                imvCompletedSprintEmpty.setVisibility(View.VISIBLE);
                imvCompletedSprintEmpty.setImageResource(R.drawable.img_empty);
            } else {
                imvCompletedSprintEmpty.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_sprint, container, false);
    }
}