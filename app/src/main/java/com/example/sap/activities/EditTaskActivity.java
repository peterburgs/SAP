package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Comment;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.adapters.CommentListAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    private static final String TAG = EditTaskActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    private CommentListAdapter commentListAdapter;
    ArrayList<Comment> commentList;
    RecyclerView rcvCommentList;

    MaterialToolbar topAppBar;
    Task task;
    Project project;
    EditText edtSummary;
    EditText edtLabel;
    EditText edtDescription;
    TextView tvTaskName;
    Spinner spnStatus;
    Spinner spnAssignee;
    Spinner spnSprint;
    ArrayAdapter<String> spnAssigneeAdapter;
    ArrayAdapter<CharSequence> spnStatusAdapter;
    ArrayAdapter<String> spnSprintAdapter;
    ArrayList<String> assignee = new ArrayList<String>();
    ArrayList<String> sprint = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        commentList = new ArrayList<>();
        rcvCommentList = findViewById(R.id.rcvComment);
        rcvCommentList.setHasFixedSize(true);
        commentListAdapter = new CommentListAdapter(commentList);

        rcvCommentList.setLayoutManager(new LinearLayoutManager(this));
        rcvCommentList.setAdapter(commentListAdapter);

        //Comment
//        spnCommentOption = findViewById(R.id.spnCommentOption);
//        ArrayAdapter<CharSequence> spnCommentOptionAdapter = ArrayAdapter.createFromResource(this, R.array.commentOption, android.R.layout.simple_spinner_dropdown_item);
//        spnCommentOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnCommentOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Object item = parent.getItemAtPosition(position);
//                //todo:Handle Options: delete || edit
//                if (item.toString().equals("Delete")) {
//                    Toast.makeText(EditTaskActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                }
//                if (item.toString().equals("Edit")) {
//                    Toast.makeText(EditTaskActivity.this, "Edited", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spnCommentOption.setAdapter(spnCommentOptionAdapter);


        edtDescription = findViewById(R.id.edtDescription);
        edtSummary = findViewById(R.id.edtSummary);
        edtLabel = findViewById(R.id.edtLabel);
        tvTaskName = findViewById(R.id.tvTaskName);
        task = null;
        project = null;
        loadingDialog = new LoadingDialog(this);

        // Spinner
        spnAssignee = findViewById(R.id.spnAssignee);
        spnAssigneeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, assignee);
        spnAssigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAssignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo: Handle Assignee
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnAssignee.setAdapter(spnAssigneeAdapter);

        // Spinner
        spnStatus = findViewById(R.id.spnStatus);
        spnStatusAdapter = ArrayAdapter.createFromResource(this, R.array.tabs, android.R.layout.simple_spinner_dropdown_item);
        spnStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo:Handle Status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnStatus.setAdapter(spnStatusAdapter);

        // Spinner
        spnSprint = findViewById(R.id.spnSprint);
        spnSprintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sprint);
        spnSprintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSprint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo:Handle Status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnSprint.setAdapter(spnSprintAdapter);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 1) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditTaskActivity.this)
                            .setIcon(R.drawable.ic_alert)
                            .setTitle("Remove Task")
                            .setMessage("Do you want to remove this task?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(EditTaskActivity.this, "Remove Successfully!", Toast.LENGTH_SHORT).show();
                                    //todo: Handle Remove Task
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EditTaskActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.show();

                }
                if (item.getOrder() == 2) {
                    Toast.makeText(EditTaskActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
                return true;
            }
        });

        taskQuery();

    }

    private void taskQuery() {
        loadingDialog.startLoadingDialog();
        Amplify.API.query(
                ModelQuery.get(Task.class, getTaskID()),
                getTaskRes -> {
                    task = getTaskRes.getData();

                    // Get project
                    Amplify.API.query(
                            ModelQuery.get(Project.class, task.getProject().getId()),
                            getProjectRes -> {
                                loadingDialog.dismissDialog();
                                runOnUiThread(() -> {
                                    project = getProjectRes.getData();
                                    edtSummary.setText(task.getSummary());
                                    edtDescription.setText(task.getDescription());
                                    edtLabel.setText(task.getLabel());
                                    tvTaskName.setText(task.getName());

                                    assignee.clear();
                                    for (ProjectParticipant member : project.getMembers()) {
                                        assignee.add(member.getMember().getUsername());
                                    }

                                    spnAssigneeAdapter.notifyDataSetChanged();

                                    sprint.clear();
                                    project.getSprints().forEach((s) -> sprint.add(s.getName()));

                                    spnSprintAdapter.notifyDataSetChanged();
                                    commentList.clear();
                                    commentList.addAll(task.getComments());

                                    commentListAdapter.notifyDataSetChanged();
                                });

                            },
                            error -> Log.e(TAG, error.toString())
                    );
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    private String getTaskID() {
        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("TASK_ID");
        }
        return newString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}