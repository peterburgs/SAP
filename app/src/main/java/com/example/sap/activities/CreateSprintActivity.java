package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.example.sap.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CreateSprintActivity extends AppCompatActivity {
    private TextInputEditText edtSprintGoal, edtSprintName;
    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sprint);

        edtSprintGoal = findViewById(R.id.edtSprintGoal);
        edtSprintName = findViewById(R.id.edtSprintName);

        loadingDialog = new LoadingDialog(this);

    }

    public void onCreateSprintClick(View view) {
        if(edtSprintName.getText().equals("")) {
            makeAlert("Name can not be empty");
            return;
        }
        if(edtSprintName.getText().toString().toLowerCase().equals("none")) {
            makeAlert("Sorry, you can not use this name");
            return;
        }
        loadingDialog.startLoadingDialog();
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {

                    // Add sprint to project
                    Sprint sprint = Sprint.builder()
                            .name(edtSprintName.getText().toString())
                            .isBacklog(false)
                            .project(getProjectRes.getData())
                            .goal(edtSprintGoal.getText().toString())
                            .build();
                    Amplify.API.mutate(
                            ModelMutation.create(sprint),
                            createSprintRes -> {
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateSprintActivity.this);
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

}