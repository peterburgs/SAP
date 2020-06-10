package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.TimeZone;

import com.example.sap.adapters.SprintTaskAdapter;

import java.util.ArrayList;


public class EditActiveSprintActivity extends AppCompatActivity {
    com.google.android.material.textfield.TextInputLayout edtDurationLayout;
    com.google.android.material.textfield.TextInputEditText edtDuration;
    private static final String TAG = EditActiveSprintActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;


    private EditText edtSprintName;
    private EditText edtSprintGoal;
    private ArrayList<Task> taskList;
    private RecyclerView rcvTaskList;
    private SprintTaskAdapter sprintTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_active_sprint);


        //Calendar
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        calendar.clear();
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);

        //Calendar Constraint
       // CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        //CalendarConstraints.DateValidator dateValidator = DateValidatorPointForward.now();
        //constraintBuilder.setValidator(dateValidator);
        //DatePicker
        MaterialDatePicker.Builder<Pair<Long, Long>> dateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        dateBuilder.setTitleText("Choose Time Range For Sprint");
        //dateBuilder.setCalendarConstraints(constraintBuilder.build());
        dateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

        final MaterialDatePicker materialDatePicker = dateBuilder.build();

        edtDuration = findViewById(R.id.edtDuration);
        edtDurationLayout = findViewById(R.id.edtDurationLayout);
        edtDurationLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        //todo: Get DatePicker values
                        edtDuration.setText(materialDatePicker.getHeaderText());
                        //Toast.makeText(EditActiveSprintActivity.this, materialDatePicker.getHeaderText(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        edtSprintName = findViewById(R.id.edtSprintName);
        edtSprintGoal = findViewById(R.id.edtSprintGoal);
        rcvTaskList = findViewById(R.id.rcvTaskList);

        loadingDialog = new LoadingDialog(this);
        taskList = new ArrayList<>();
        sprintTaskAdapter = new SprintTaskAdapter(getApplicationContext(), taskList);
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
                        sprintTaskAdapter = new SprintTaskAdapter(this, taskList);
                        rcvTaskList.setAdapter(sprintTaskAdapter);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActiveSprintActivity.this);
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