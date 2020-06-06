package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Comment;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;
import com.example.sap.adapters.CommentListAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class BacklogEditTaskActivity extends AppCompatActivity {

    private static final String TAG = BacklogEditTaskActivity.class.getSimpleName();
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
    com.google.android.material.textfield.TextInputLayout edtCommentLayout;
    com.google.android.material.textfield.TextInputEditText edtComment;
    Spinner spnAssignee;
    ArrayAdapter<String> spnAssigneeAdapter;
    ArrayList<String> assigneeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog_edit_task);

        edtComment = findViewById(R.id.edtComment);
        edtCommentLayout = findViewById(R.id.edtCommentLayout);

        edtCommentLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtComment.setText("");
                closeKeyboard();
                Toast.makeText(BacklogEditTaskActivity.this, "Comment Uploaded!", Toast.LENGTH_SHORT).show();
            }
        });

        commentList = new ArrayList<>();
        rcvCommentList = findViewById(R.id.rcvComment);
        rcvCommentList.setHasFixedSize(true);
        commentListAdapter = new CommentListAdapter(commentList);

        rcvCommentList.setLayoutManager(new LinearLayoutManager(this));
        rcvCommentList.setAdapter(commentListAdapter);

        edtDescription = findViewById(R.id.edtDescription);
        edtSummary = findViewById(R.id.edtSummary);
        edtLabel = findViewById(R.id.edtLabel);
        task = null;
        project = null;
        loadingDialog = new LoadingDialog(this);

        assigneeList = new ArrayList<>();
        // Spinner
        spnAssignee = findViewById(R.id.spnAssignee);
        spnAssigneeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, assigneeList);
        spnAssigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAssignee.setAdapter(spnAssigneeAdapter);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 1) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BacklogEditTaskActivity.this)
                            .setIcon(R.drawable.ic_alert)
                            .setTitle("Remove Task")
                            .setMessage("Do you want to remove this task?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onDeleteTaskClick();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();

                }
                if (item.getOrder() == 2) {
                    onSaveTaskClick();
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

                                    assigneeList.clear();
                                    for (ProjectParticipant member : project.getMembers()) {
                                        assigneeList.add(member.getMember().getUsername());
                                    }

                                    spnAssigneeAdapter.notifyDataSetChanged();
                                    spnAssignee.setSelection(assigneeList.indexOf(task.getAssignee().getUsername()));

                                    commentList.clear();
                                    commentList.addAll(task.getComments());
                                    commentListAdapter.notifyDataSetChanged();

                                    topAppBar.setTitle(getTaskRes.getData().getName());
                                });

                            },
                            error -> Log.e(TAG, error.toString())
                    );
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    private void onDeleteTaskClick() {
        loadingDialog.startLoadingDialog();
        // Delete task
        Amplify.API.mutate(
                ModelMutation.delete(task),
                response -> {
                    loadingDialog.dismissDialog();
                    runOnUiThread(this::onBackPressed);
                },
                error -> Log.e(TAG, error.toString())
        );
    }


    private void onSaveTaskClick() {
        loadingDialog.startLoadingDialog();
        // Get assignee
        Amplify.API.query(
                ModelQuery.list(User.class, User.USERNAME.contains(spnAssignee.getSelectedItem().toString())),
                getAssigneeRes -> {
                    User selectedAssignee = ((ArrayList<User>)getAssigneeRes.getData()).get(0);

                    // Update Task
                    Task taskMutation = Task.builder()
                            .name(task.getName())
                            .summary(edtSummary.getText().toString())
                            .project(task.getProject())
                            .assignee(selectedAssignee)
                            .sprint(task.getSprint())
                            .description(edtDescription.getText().toString())
                            .label(edtLabel.getText().toString())
                            .id(task.getId())
                            .build();

                    Amplify.API.mutate(
                            ModelMutation.update(taskMutation),
                            updateTaskRes -> {
                                loadingDialog.dismissDialog();
                                runOnUiThread(this::onBackPressed);
                            },
                            error -> {
                                Log.e(TAG, error.toString());
                            }
                    );
                },
                error -> {
                    Log.e(TAG, error.toString());
                }
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

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onMoveToActiveSprint(View view) {
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
                                Sprint activeSprint = null;
                                for(Sprint sprint: getProjectRes.getData().getSprints()) {
                                    if(sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                        activeSprint = sprint;
                                    }
                                }

                                // Update Task
                                Task taskMutation = Task.builder()
                                        .name(task.getName())
                                        .summary(task.getSummary())
                                        .project(task.getProject())
                                        .assignee(task.getAssignee())
                                        .sprint(activeSprint)
                                        .description(task.getDescription())
                                        .label(task.getLabel())
                                        .status(TaskStatus.TODO)
                                        .id(task.getId())
                                        .build();

                                Amplify.API.mutate(
                                        ModelMutation.update(taskMutation),
                                        updateTaskRes -> {
                                            loadingDialog.dismissDialog();
                                            runOnUiThread(this::onBackPressed);
                                        },
                                        error -> {
                                            Log.e(TAG, error.toString());
                                        }
                                );

                            },
                            error -> Log.e(TAG, error.toString())
                    );
                },
                error -> Log.e(TAG, error.toString())
        );
    }

}