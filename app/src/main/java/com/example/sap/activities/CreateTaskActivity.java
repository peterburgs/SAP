package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;

public class CreateTaskActivity extends AppCompatActivity {
    EditText edtSummary;
    EditText edtDescription;
    EditText edtEstimatedTime;
    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        loadingDialog = new LoadingDialog(this);

        edtSummary = findViewById(R.id.edtSummary);
        edtDescription = findViewById(R.id.edtDescription);
        edtEstimatedTime = findViewById(R.id.edtEstimatedTime);
    }

    public void onCreateTaskClick(View view) {

        // Validation
        if (edtSummary.getText().toString().equals("")) {
            makeAlert("Summary cannot be empty");
            return;
        }
        if (!edtEstimatedTime.getText().toString().equals("")) {
            try {
                if(Float.parseFloat(edtEstimatedTime.getText().toString()) <= 0) {
                    makeAlert("Estimated time must be greater than 0!");
                    return;
                }
            } catch (NumberFormatException e) {
                makeAlert("Estimated time is incorrect format!");
                return;
            }
        }

        loadingDialog.startLoadingDialog();
        // Get project
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {

                    // Get current user
                    Amplify.API.query(
                            ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                            getUserRes -> {

                                Sprint backlog = null;
                                for(Sprint sprint: getProjectRes.getData().getSprints()) {
                                    if(sprint.getIsBacklog()) {
                                        backlog = sprint;
                                    }
                                }

                                int numofTasks = getProjectRes.getData().getTasks().size() + 1;
                                String taskName = getProjectRes.getData().getKey() + "-" + String.valueOf(numofTasks);

                                Task task = Task.builder()
                                        .name(taskName)
                                        .summary(edtSummary.getText().toString())
                                        .project(getProjectRes.getData())
                                        .assignee(getUserRes.getData())
                                        .sprint(backlog)
                                        .description(edtDescription.getText().toString())
                                        .estimatedTime(edtEstimatedTime.getText().toString().equals("") ? 0f : Float.parseFloat(edtEstimatedTime.getText().toString()))
                                        .realWorkingTime(0f)
                                        .build();
                                Amplify.API.mutate(
                                        ModelMutation.create(task),
                                        response -> {
                                            loadingDialog.dismissDialog();
                                            runOnUiThread(this::onBackPressed);
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
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );

    }

    private String getProjectID() {
        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("PROJECT_ID");
        }
        return newString;
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTaskActivity.this);
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