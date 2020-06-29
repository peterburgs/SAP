package com.example.sap.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Role;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.sap.R;
import com.example.sap.adapters.ParticipantListAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    TextView edtProjectName;
    TextView edtProjectKey;
    ImageView imvProjectAvatar;
    RecyclerView rcvProjectParticipant;
    MaterialToolbar topAppBar;
    MaterialButton btnInviteParticipant;
    MaterialButton btnRemoveProject;
    private ArrayList<ProjectParticipant> participantList;
    private ParticipantListAdapter participantListAdapter;
    private com.anychart.AnyChartView pcProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edtProjectName = findViewById(R.id.edtProjectName);
        edtProjectName.setMovementMethod(new ScrollingMovementMethod());
        edtProjectKey = findViewById(R.id.edtProjectKey);
        imvProjectAvatar = findViewById(R.id.imvProjectAvatar);
        btnInviteParticipant = findViewById(R.id.btnInviteParticipant);
        btnRemoveProject = findViewById(R.id.btnRemoveProject);
        rcvProjectParticipant = findViewById(R.id.rcvProjectParticipant);
        topAppBar = findViewById(R.id.topAppBar);
        pcProgress = findViewById(R.id.pcProgress);
        loadingDialog = new LoadingDialog(this);
        participantList = new ArrayList<>();
        participantListAdapter = new ParticipantListAdapter(this, participantList);
        rcvProjectParticipant.setAdapter(participantListAdapter);
        rcvProjectParticipant.setLayoutManager(new LinearLayoutManager(this));

        participantListAdapter.setOnItemClickListener(new ParticipantListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(Amplify.Auth.getCurrentUser().getUsername().equals(participantList.get(position).getMember().getUsername())) {
                    makeAlert("You cannot delete yourself. If you wish to delete the project, click remove button instead.");
                } else {
                    loadingDialog.startLoadingDialog();

                    // Delete participant
                    Amplify.API.mutate(
                            ModelMutation.delete(participantList.get(position)),
                            deleteParticipant -> {
                                if(deleteParticipant.getData() == null) {
                                    runOnUiThread(() -> {
                                        makeAlert("You are not allowed to remove any member");
                                        loadingDialog.dismissDialog();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SettingActivity.this, "Participant has been remove successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
                                    });
                                }
                            },
                            error -> {
                                loadingDialog.dismissDialog();
                                Log.e(TAG, "Error", error);
                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                            }
                    );
                }
            }
        });

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onSaveProjectClick();
                return true;
            }
        });

        query();
    }

    private void onSaveProjectClick() {
        loadingDialog.startLoadingDialog();
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {
                    Project project = Project.builder()
                            .name(edtProjectName.getText().toString())
                            .key(getProjectRes.getData().getKey())
                            .avatarKey(getProjectRes.getData().getAvatarKey())
                            .id(getProjectRes.getData().getId())
                            .build();

                    // Save project
                    Amplify.API.mutate(
                            ModelMutation.update(project),
                            updateProjectRes -> {
                                loadingDialog.dismissDialog();
                                Intent intent = new Intent(SettingActivity.this, ProjectContainerActivity.class);
                                intent.putExtra("PROJECT_ID", getProjectID());
                                startActivity(intent);
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

    private void query() {
        loadingDialog.startLoadingDialog();
        // Get project
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {
                    participantList.clear();
                    participantList.addAll(getProjectRes.getData().getMembers());
                    runOnUiThread(() -> {
                        edtProjectName.setText(getProjectRes.getData().getName());
                        edtProjectKey.setText(getProjectRes.getData().getKey());
                        participantListAdapter.notifyDataSetChanged();
                    });
                    Amplify.Storage.getUrl(
                            getProjectRes.getData().getAvatarKey(),
                            result -> {
                                loadingDialog.dismissDialog();
                                runOnUiThread(() -> {
                                    Picasso.get().load(result.getUrl().toString()).into(imvProjectAvatar);
                                });
                            },
                            error -> {
                                Log.e("GetProjectImageError", "Error", error);
                            }
                    );
                    for (ProjectParticipant projectParticipant : getProjectRes.getData().getMembers()) {
                        if (projectParticipant.getRole().equals(Role.PROJECT_LEADER)) {
                            if (!Amplify.Auth.getCurrentUser().getUsername().equals(projectParticipant.getMember().getUsername())) {
                                btnInviteParticipant.setClickable(false);
                                btnInviteParticipant.setBackgroundColor(Color.GRAY);
                                btnRemoveProject.setClickable(false);
                                btnRemoveProject.setBackgroundColor(Color.GRAY);
                            }
                        }
                    }

                    //Get All Tasks of the active sprint
                    ArrayList<Task> taskList = new ArrayList<Task>();
                    for (Sprint s : getProjectRes.getData().getSprints()) {
                        if (s.getIsCompleted() != null && s.getIsStarted() != null && !s.getIsCompleted() && s.getIsStarted()) {
                            taskList.addAll(s.getTasks());
                        }
                    }
                    runOnUiThread(() -> {
                        topAppBar.setTitle(getProjectRes.getData().getKey() + " Information");
                        setupPieChart(taskList);
                    });
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }


    public void onRemoveProject(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("Do you want to remove this project?");
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Todo: Remove project might cause bug
                loadingDialog.startLoadingDialog();
                //Get Project
                Amplify.API.query(
                        ModelQuery.get(Project.class, getProjectID()),
                        getProjectRes -> {
                            Amplify.API.mutate(
                                    ModelMutation.delete(getProjectRes.getData()),
                                    deleteProjectRes -> {
                                        loadingDialog.dismissDialog();
                                        Intent intent = new Intent(SettingActivity.this, ProjectListActivity.class);
                                        startActivity(intent);
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

                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SettingActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onInviteParticipant(View view) {
        Intent intent = new Intent(getApplicationContext(), InviteParticipantActivity.class);
        intent.putExtra("PROJECT_ID", getProjectID());
        startActivity(intent);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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

    public void setupPieChart(ArrayList<Task> taskList) {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntryList = new ArrayList<>();

        //Count To_do Task
        int todoNum = 0;
        int inProgressNum = 0;
        int doneNum = 0;
        for (Task t : taskList) {
            if (t.getStatus().equals(TaskStatus.TODO)) {
                todoNum++;
            }
            if (t.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                inProgressNum++;
            }
            if (t.getStatus().equals(TaskStatus.DONE)) {
                doneNum++;
            }
        }
        dataEntryList.add(new ValueDataEntry("Todo", todoNum));
        dataEntryList.add(new ValueDataEntry("In Progress", inProgressNum));
        dataEntryList.add(new ValueDataEntry("Done", doneNum));
        pie.data(dataEntryList);
        pcProgress.setChart(pie);
    }

}