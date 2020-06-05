package com.example.sap.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sap.R;
import com.example.sap.adapters.InProgressAdapter;

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

    //Hardcoded Data:
    String taskName[] = {"SAP-1", "SAP-2", "SAP-3", "SAP-4", "SAP-1", "SAP-2", "SAP-3", "SAP-4"};
    String taskSummary[] = {"Design raw UI", "Apply Material Design Component", "Add Constraints", "Beautify", "Design raw UI", "Apply Material Design Component", "Add Constraints", "Beautify"};
    String taskLabel[] = {"Design UI", "Design UI", "Design UI", "Design UI", "Design UI", "Design UI", "Design UI", "Design UI"};
    String assignee[] = {"peterburgs", "peterburgs", "peterburgs", "starea", "peterburgs", "peterburgs", "peterburgs", "starea"};

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
        rcvInProgress = getView().findViewById(R.id.rcvInProgress);
        InProgressAdapter inProgressAdapter=new InProgressAdapter(getContext(), taskName, taskSummary, taskLabel, assignee);

        rcvInProgress.setAdapter(inProgressAdapter);
        rcvInProgress.setLayoutManager(new LinearLayoutManager(getContext()));

        inProgressAdapter.setOnItemClickListener(new InProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //todo: handle Nav to EditTask
                Toast.makeText(getContext(), "Task Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress, container, false);
    }
}