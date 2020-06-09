package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Role;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;

public class CreateProjectActivity extends AppCompatActivity {

    private static final String TAG = CreateProjectActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    EditText edt_projectName;
    EditText edt_projectKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        edt_projectName = findViewById(R.id.edt_projectName);
        edt_projectKey = findViewById(R.id.edt_projectKey);

        loadingDialog = new LoadingDialog(this);
        
    }

    /*
    * Create a new project and set current user as a project leader
    * */
    public void onCreateProjectClick(View view) {
        String projectName = edt_projectName.getText().toString();
        String projectKey = edt_projectKey.getText().toString();

        if(projectName.equals("") || projectKey.equals("")) {
            makeAlert("Name or key cannot be empty");
            return;
        }

        loadingDialog.startLoadingDialog();
        //Get current user
        Amplify.API.query(
                ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                getUserRes -> {
                    Project project = Project.builder()
                            .name(projectName)
                            .key(projectKey)
                            .avatarKey(getRandomAvatarKey())
                            .build();

                    // Add new project
                    Amplify.API.mutate(
                            ModelMutation.create(project),
                            createProjectRes -> {
                                Sprint sprint = Sprint.builder()
                                        .name("None")
                                        .isBacklog(true)
                                        .project(createProjectRes.getData())
                                        .build();

                                // Create backlog for the project
                                Amplify.API.mutate(
                                        ModelMutation.create(sprint),
                                        createBacklogRes -> {
                                            Log.i("Success", "Create backlog for project successfully");
                                        },
                                        error -> {
                                            Log.e(TAG, "Error", error);
                                        }
                                );

                                ProjectParticipant projectParticipant = ProjectParticipant.builder()
                                        .role(Role.PROJECT_LEADER)
                                        .project(createProjectRes.getData())
                                        .member(getUserRes.getData())
                                        .build();

                                // Add new project and user to project participant
                                Amplify.API.mutate(
                                        ModelMutation.create(projectParticipant),
                                        createProjectParticipantRes -> {
                                            loadingDialog.dismissDialog();
                                            Intent intent = new Intent(getApplicationContext(), ProjectListActivity.class);
                                            startActivity(intent);
                                        },
                                        error -> {
                                            Log.e(TAG, "Error", error);
                                            runOnUiThread(() -> makeAlert(error.getCause().toString()));
                                        }
                                );
                            },
                            error -> {
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
                },
                error -> {
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }

    private String getRandomAvatarKey() {
        int index = (int)(Math.random() * ((5 - 1) + 1)) + 1;
        return "project_img"+index+".png";

    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProjectActivity.this);
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