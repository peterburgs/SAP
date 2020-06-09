package com.example.sap.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;
import com.example.sap.adapters.ProjectListAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private static final String TAG = ProjectListActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    private ProjectListAdapter projectListAdapter;
    ArrayList<Project> projectList;

    RecyclerView rcvProjectList;
    MaterialToolbar topAppBar;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        projectList = new ArrayList<>();
        rcvProjectList = findViewById(R.id.rcvProjectList);
        rcvProjectList.setHasFixedSize(true);
        projectListAdapter = new ProjectListAdapter(projectList);

        rcvProjectList.setLayoutManager(new LinearLayoutManager(this));
        rcvProjectList.setAdapter(projectListAdapter);
        projectListAdapter.setOnItemClickListener(new ProjectListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), ProjectContainerActivity.class);
                intent.putExtra("PROJECT_ID", projectList.get(position).getId());
                startActivity(intent);
            }
        });


        loadingDialog = new LoadingDialog(this);

        topAppBar = findViewById(R.id.topAppBar);

        //fabProject = findViewById(R.id.fabProject);
        fabAccount = findViewById(R.id.fabAccount);

        fabAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(intent);
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), CreateProjectActivity.class);
                startActivity(intent);
                return true;
            }
        });

        projectListQuery();
        projectCreateSubscribe();
        projectUpdateSubscribe();
        projectDeleteSubscribe();
    }

    /*
     * Get project list from the cloud according to the current user
     * And update recycler view
     * */
    private void projectListQuery() {
        // Get project list
        loadingDialog.startLoadingDialog();
        Amplify.API.query(
                ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                response -> {
                    loadingDialog.dismissDialog();
                    if (response.getData() != null) {
                        projectList.clear();
                        for (ProjectParticipant p : response.getData().getProjects()) {
                            projectList.add(p.getProject());
                        }

                        runOnUiThread(() -> {
                            projectListAdapter.notifyDataSetChanged();
                        });
                    }
                },
                error -> {
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }

    private void projectCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Project.class),
                onEstablished -> Log.i("OnCreateProjectSubscribe", "Subscription established"),
                onCreated -> {
                    Amplify.API.query(
                            ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                            response -> {
                                if (response.getData() != null) {
                                    projectList.clear();
                                    for (ProjectParticipant p : response.getData().getProjects()) {
                                        projectList.add(p.getProject());
                                    }

                                    runOnUiThread(() -> {
                                        projectListAdapter.notifyDataSetChanged();
                                    });
                                }
                            },
                            error -> {
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
                },
                onFailure -> Log.e("OnCreateProjectSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnCreateProjectSubscribe", "Subscription completed")
        );
    }

    private void projectUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Project.class),
                onEstablished -> Log.i("OnUpdateProjectSubscribe", "Subscription established"),
                onCreated -> {
                    Amplify.API.query(
                            ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                            response -> {
                                if (response.getData() != null) {
                                    projectList.clear();
                                    for (ProjectParticipant p : response.getData().getProjects()) {
                                        projectList.add(p.getProject());
                                    }

                                    runOnUiThread(() -> {
                                        projectListAdapter.notifyDataSetChanged();
                                    });
                                }
                            },
                            error -> {
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
                },
                onFailure -> Log.e("OnUpdateProjectSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateProjectSubscribe", "Subscription completed")
        );
    }

    private void projectDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Project.class),
                onEstablished -> Log.i("OnDeleteProjectSubscribe", "Subscription established"),
                onCreated -> {
                    Amplify.API.query(
                            ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                            response -> {
                                if (response.getData() != null) {
                                    projectList.clear();
                                    for (ProjectParticipant p : response.getData().getProjects()) {
                                        projectList.add(p.getProject());
                                    }

                                    runOnUiThread(() -> {
                                        projectListAdapter.notifyDataSetChanged();
                                    });
                                }
                            },
                            error -> {
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
                },
                onFailure -> Log.e("OnDeleteProjectSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteProjectSubscribe", "Subscription completed")
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}