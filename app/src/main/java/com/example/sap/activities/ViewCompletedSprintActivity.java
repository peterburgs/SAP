package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.adapters.SprintTaskAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewCompletedSprintActivity extends AppCompatActivity {

    private static final String TAG = EditFutureSprintActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    com.google.android.material.textfield.TextInputLayout edtDurationLayout;
    com.google.android.material.textfield.TextInputEditText edtDuration;

    private EditText edtSprintName;
    private EditText edtSprintGoal;
    private ArrayList<Task> taskList;
    private RecyclerView rcvTaskList;
    private SprintTaskAdapter sprintTaskAdapter;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_completed_sprint);

        edtDuration = findViewById(R.id.edtDuration);
        edtDuration.setInputType(InputType.TYPE_NULL);
        edtDuration.setTextIsSelectable(true);
        edtDurationLayout = findViewById(R.id.edtDurationLayout);

        edtSprintName = findViewById(R.id.edtSprintName);
        edtSprintGoal = findViewById(R.id.edtSprintGoal);
        rcvTaskList = findViewById(R.id.rcvTaskList);
        topAppBar = findViewById(R.id.topAppBar);

        loadingDialog = new LoadingDialog(this);
        taskList = new ArrayList<>();
        sprintTaskAdapter = new SprintTaskAdapter(this, taskList);
        rcvTaskList.setAdapter(sprintTaskAdapter);
        rcvTaskList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sprintTaskAdapter.setOnItemClickListener(new SprintTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), SprintEditTaskActivity.class);
                intent.putExtra("TASK_ID", taskList.get(position).getId());
                startActivity(intent);
            }
        });

        query();
    }

    private void query() {
        loadingDialog.startLoadingDialog();
        // Get sprint
        Amplify.API.query(
                ModelQuery.get(Sprint.class, getSprintID()),
                getSprintRes -> {
                    taskList.clear();
                    taskList.addAll(getSprintRes.getData().getTasks());

                    runOnUiThread(() -> {
                        loadingDialog.dismissDialog();
                        edtSprintName.setText(getSprintRes.getData().getName());
                        edtSprintGoal.setText(getSprintRes.getData().getGoal());
                        sprintTaskAdapter.notifyDataSetChanged();
                        topAppBar.setTitle(getSprintRes.getData().getName());
                        String duration = getSprintRes.getData().getStartDate().format() + " to " + getSprintRes.getData().getEndDate().format();
                        edtDuration.setText(duration);
                    });
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }

    private String getSprintID() {
        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("SPRINT_ID");
        }
        return newString;
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewCompletedSprintActivity.this);
        builder.setMessage(content);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}