package com.example.sap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    List<Project> projectList;


    RecyclerView rcvProjectList;
    MaterialToolbar topAppBar;
    com.getbase.floatingactionbutton.FloatingActionButton fabProject;
    com.getbase.floatingactionbutton.FloatingActionButton fabTask;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        rcvProjectList = findViewById(R.id.rcvProjectList);
        topAppBar = findViewById(R.id.topAppBar);
        fabProject = findViewById(R.id.fabProject);
        fabTask = findViewById(R.id.fabTask);
        fabAccount = findViewById(R.id.fabAccount);


        //todo: Handle FAB Menu here
        fabProject.setOnClickListener(v -> {
            Toast.makeText(this, "Project Selected", Toast.LENGTH_SHORT).show();
        });
        fabTask.setOnClickListener(v -> {
            Toast.makeText(this, "Task Selected", Toast.LENGTH_SHORT).show();
        });
        fabAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Account Selected", Toast.LENGTH_SHORT).show();
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), CreateProjectActivity.class);
                startActivity(intent);
                return true;
            }
        });

        projectList = new ArrayList<>();

        loadingDialog = new LoadingDialog(this);

        query();

    }

    @Override
    protected void onRestart() {
        query();
        super.onRestart();
    }

    private void query() {
        loadingDialog.startLoadingDialog();
        // Get projects by user id
        Amplify.API.query(
                ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                response -> {
                    loadingDialog.dismissDialog();
                    List<Project> projectList = new ArrayList<>();

                    // Configure internal storage
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                    for (Project project : response.getData().getLeadingProjects()) {
                        // Get avatar of the project and update UI
                        File path = new File(directory, project.getAvatarKey());
                        Amplify.Storage.downloadFile(
                                project.getAvatarKey(),
                                path,
                                result -> {
                                    // Add project to project list
                                    projectList.add(project);
                                    projectList.sort((o1, o2) -> ((Date) o1.getCreatedAt().toDate()).compareTo(((Date) o2.getCreatedAt().toDate())));
                                    runOnUiThread(() -> {
                                        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, projectList);
                                        rcvProjectList.setAdapter(projectListAdapter);
                                        rcvProjectList.setLayoutManager(new LinearLayoutManager(this));
                                    });
                                },
                                error -> Log.e("MyAmplifyApp", "Download Failure", error)
                        );
                    }
                },
                error -> {
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }

    private void getProjectsAvatar(List<Project> projectList) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        for (Project project : projectList) {
            // Create imageDir
            File path = new File(directory, project.getAvatarKey());
            Amplify.Storage.downloadFile(
                    project.getAvatarKey(),
                    path,
                    result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName()),
                    error -> Log.e("MyAmplifyApp", "Download Failure", error)
            );
        }
    }

    private void makeToast(Toast toast, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectListActivity.this);
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