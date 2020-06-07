package com.example.sap.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.example.sap.R;
import com.example.sap.adapters.ActiveSprintAdapter;
import com.example.sap.adapters.FutureSprintAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FutureSprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FutureSprintFragment extends Fragment {

    private static final String SPRINT_LIST = "sprintList";
    private ArrayList<Sprint> mSprintList;
    RecyclerView rcvFutureSprint;
    private FutureSprintAdapter futureSprintAdapter;
    private Handler mHandler;
    private TextView tvDayRemaining;
    private ImageView imvFutureSprintEmpty;

    public FutureSprintFragment() {
        // Required empty public constructor
    }

    public static FutureSprintFragment newInstance(ArrayList<Sprint> sprintList) {
        FutureSprintFragment fragment = new FutureSprintFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_future_sprint, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        rcvFutureSprint = getView().findViewById(R.id.rcvFutureSprint);
        tvDayRemaining = getView().findViewById(R.id.tvFutureSprintRemainingDay);
        imvFutureSprintEmpty = getView().findViewById(R.id.imvFutureSprintEmpty);
        futureSprintAdapter = new FutureSprintAdapter(getContext(), mSprintList);

        rcvFutureSprint.setAdapter(futureSprintAdapter);
        rcvFutureSprint.setLayoutManager(new LinearLayoutManager(getContext()));
        futureSprintAdapter.setOnItemClickListener(new FutureSprintAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: Open  Sprint Detail Activity
            }
        });
        mHandler.post(() -> {
            futureSprintAdapter.notifyDataSetChanged();
            if (mSprintList.isEmpty()) {
                imvFutureSprintEmpty.setImageResource(R.drawable.img_empty);
            } else {
                if (!mSprintList.isEmpty()) {
                    //
                    //todo: Backend get Remaining days
                }
            }
        });
        //todo: Add Subscribers to update data
    }
}