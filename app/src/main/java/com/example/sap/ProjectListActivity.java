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
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {

    private static final String TAG = ProjectListActivity.class.getSimpleName();
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

        projectListQuery(Amplify.Auth.getCurrentUser().getUserId());

    }

    @Override
    protected void onRestart() {
        projectListQuery(Amplify.Auth.getCurrentUser().getUserId());
        super.onRestart();
    }

    /*
    * Get project list from the cloud according to the current user
    * And update recycler view
    * */
    private void projectListQuery(String userId) {
        // Get project list
        Amplify.API.query(
                ModelQuery.get(User.class, userId),
                response -> {
                    List<Project> projectList = new ArrayList<>();
                    for(ProjectParticipant p : response.getData().getProjects()) {
                        projectList.add(p.getProject());
                    }

                    runOnUiThread(() -> {
                        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, projectList);
                        rcvProjectList.setAdapter(projectListAdapter);
                        rcvProjectList.setLayoutManager(new LinearLayoutManager(this));
                    });

                },
                error -> {
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
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