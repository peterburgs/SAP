package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.adapters.SprintTaskAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Date;

public class EditFutureSprintActivity extends AppCompatActivity {

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
    MaterialDatePicker.Builder<Pair<Long, Long>> dateBuilder;
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_future_sprint);

        dateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        dateBuilder.setTitleText("Choose Time Range For Sprint");
        dateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);
        materialDatePicker = dateBuilder.build();

        edtDuration = findViewById(R.id.edtDuration);
        edtDuration.setInputType(InputType.TYPE_NULL);
        edtDuration.setTextIsSelectable(true);
        edtDurationLayout = findViewById(R.id.edtDurationLayout);
        edtDurationLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        edtDuration.setText(materialDatePicker.getHeaderText());
                    }
                });
            }
        });

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

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onSaveSprintClick();
                return true;
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
                    });
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }

    public void onSaveSprintClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFutureSprintActivity.this);
        builder.setMessage("Duration will not be saved. It is used for start sprint action");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                loadingDialog.startLoadingDialog();
                // Get sprint
                Amplify.API.query(
                        ModelQuery.get(Sprint.class, getSprintID()),
                        getSprintRes -> {

                            Sprint sprint = Sprint.builder()
                                    .name(edtSprintName.getText().toString())
                                    .isBacklog(false)
                                    .project(getSprintRes.getData().getProject())
                                    .goal(edtSprintGoal.getText().toString())
                                    .id(getSprintRes.getData().getId())
                                    .build();

                            // Save Sprint
                            Amplify.API.mutate(
                                    ModelMutation.update(sprint),
                                    updateSprintRes -> {
                                        loadingDialog.dismissDialog();
                                        runOnUiThread(() -> onBackPressed());
                                    },
                                    error -> {
                                        loadingDialog.dismissDialog();
                                        Log.e(TAG, "Error", error);
                                        runOnUiThread(() -> makeAlert(error.getCause().toString()));
                                    }
                            );
                        },
                        error -> {
                            loadingDialog.dismissDialog();
                            Log.e(TAG, "Error", error);
                            runOnUiThread(() -> makeAlert(error.getCause().toString()));
                        }
                );
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onStartSprintClick(View view) {
        if(edtDuration.getText().toString().equals("")) {
            makeAlert("Please choose a duration first");
            return;
        }
        Temporal.Date startDate = new Temporal.Date(new Date(materialDatePicker.getSelection().first));
        Temporal.Date endDate = new Temporal.Date(new Date(materialDatePicker.getSelection().second));
        loadingDialog.startLoadingDialog();
        // Get Sprint
        Amplify.API.query(
                ModelQuery.get(Sprint.class, getSprintID()),
                getSprintRes -> {

                    // Get Project
                    Amplify.API.query(
                            ModelQuery.get(Project.class, getSprintRes.getData().getProject().getId()),
                            getProjectRes -> {
                                boolean isAnyActiveSprint = false;
                                for(Sprint sprint : getProjectRes.getData().getSprints()) {
                                    if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                        isAnyActiveSprint = true;
                                        break;
                                    }
                                }
                                if(isAnyActiveSprint) {
                                    loadingDialog.dismissDialog();
                                    makeAlert("You have to complete the current active sprint first");
                                } else {
                                    Sprint sprint = Sprint.builder()
                                            .name(edtSprintName.getText().toString())
                                            .isBacklog(false)
                                            .project(getProjectRes.getData())
                                            .startDate(startDate)
                                            .endDate(endDate)
                                            .goal(edtSprintGoal.getText().toString())
                                            .id(getSprintRes.getData().getId())
                                            .isStarted(true)
                                            .isCompleted(false)
                                            .build();

                                    // Save sprint
                                    Amplify.API.mutate(
                                            ModelMutation.update(sprint),
                                            updateSprint -> {
                                                loadingDialog.dismissDialog();
                                                runOnUiThread(this::onBackPressed);
                                            },
                                            error -> {
                                                loadingDialog.dismissDialog();
                                                Log.e(TAG, "Error", error);
                                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                                            }
                                    );
                                }
                            },
                            error -> {
                                loadingDialog.dismissDialog();
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFutureSprintActivity.this);
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