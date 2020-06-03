package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Role;
import com.amplifyframework.datastore.generated.model.User;

import java.util.HashMap;

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
    private void createProjectMutation(String projectName, String projectKey) {
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
                                ProjectParticipant projectParticipant = ProjectParticipant.builder()
                                        .role(Role.PROJECT_LEADER)
                                        .project(createProjectRes.getData())
                                        .member(getUserRes.getData())
                                        .build();
                                // Add new project and user to project participant
                                Amplify.API.mutate(
                                        ModelMutation.create(projectParticipant),
                                        createProjectParticipantRes -> {
                                            Log.i("Success", "Create Project successfully");
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