package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
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
import com.amplifyframework.api.graphql.model.ModelSubscription;
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
    ArrayAdapter<User> spnAssigneeAdapter;
    ArrayList<User> assigneeList;
    Spinner spnSprint;
    ArrayAdapter<Sprint> spnSprintAdapter;
    ArrayList<Sprint> sprintList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog_edit_task);

        edtComment = findViewById(R.id.edtComment);
        edtCommentLayout = findViewById(R.id.edtCommentLayout);

        edtCommentLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentMutation(edtComment.getText().toString());
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

        sprintList = new ArrayList<>();
        // Spinner
        spnSprint = findViewById(R.id.spnSprint);
        spnSprintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sprintList);
        spnSprintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSprint.setAdapter(spnSprintAdapter);

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
        commentCreateSubscribe();
        commentDeleteSubscribe();

        commentListAdapter.setOnItemClickListener((position -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BacklogEditTaskActivity.this);
            builder.setMessage("Do you want to remove this comment?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get current user
                    loadingDialog.startLoadingDialog();
                    Amplify.API.query(
                            ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                            getCurrentUserRes -> {
                                if(!getCurrentUserRes.getData().equals(commentList.get(position).getAuthor())) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(BacklogEditTaskActivity.this);
                                    builder2.setMessage("You are not allowed to delete this comment");
                                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    AlertDialog dialog2 = builder2.create();
                                    dialog2.show();
                                } else {
                                    Amplify.API.mutate(
                                            ModelMutation.delete(commentList.get(position)),
                                            deleteCommentRes -> {
                                                loadingDialog.dismissDialog();
                                                runOnUiThread(() -> {
                                                    Toast.makeText(BacklogEditTaskActivity.this, "Delete comment successfully", Toast.LENGTH_SHORT).show();
                                                });
                                            },
                                            error -> Log.e("DeleteCommentError", error.toString())
                                    );
                                }
                            },
                            error -> Log.e("DeleteCommentError", error.toString())
                    );
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }));
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

                                    // Data for assignee spinner
                                    assigneeList.clear();
                                    for (ProjectParticipant member : project.getMembers()) {
                                        assigneeList.add(member.getMember());
                                    }
                                    spnAssigneeAdapter.notifyDataSetChanged();
                                    spnAssignee.setSelection(assigneeList.indexOf(task.getAssignee()));

                                    // Data for sprint spinner
                                    sprintList.clear();
                                    for(Sprint sprint: project.getSprints()) {
                                        if(!sprint.getIsBacklog()) {
                                            sprintList.add(sprint);
                                        }
                                    }
                                    spnSprintAdapter.notifyDataSetChanged();

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

    private void commentMutation(String content) {
        loadingDialog.startLoadingDialog();
        // Get User
        Amplify.API.mutate(
                ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                getUserRes -> {

                    // Add comment
                    Comment comment = Comment.builder()
                            .content(content)
                            .author(getUserRes.getData())
                            .task(task)
                            .build();

                    Amplify.API.mutate(
                            ModelMutation.create(comment),
                            createCommentRes -> {
                                loadingDialog.dismissDialog();
                                runOnUiThread(() -> {
                                    edtComment.setText("");
                                });
                                closeKeyboard();
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
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, task.getProject().getId()),
                getProjectRes -> {

                    User selectedAssignee=null;
                    Sprint selectedSprint = null;
                    for(ProjectParticipant projectParticipant: getProjectRes.getData().getMembers()) {
                        if(projectParticipant.getMember().equals(spnAssignee.getSelectedItem())) {
                            selectedAssignee = projectParticipant.getMember();
                        }
                    }

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

    private void commentCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Comment.class),
                onEstablished -> Log.i("commentCreateSubscribe", "Subscription established"),
                onUpdated -> {
                    Amplify.API.query(
                            ModelQuery.get(Task.class, getTaskID()),
                            getTaskRes -> {
                                task = getTaskRes.getData();

                                runOnUiThread(() -> {
                                    commentList.clear();
                                    commentList.addAll(task.getComments());
                                    commentListAdapter.notifyDataSetChanged();
                                });
                            },
                            error -> Log.e(TAG, error.toString())
                    );
                },
                onFailure -> Log.e("commentCreateSubscribe", "Subscription failed", onFailure),
                () -> Log.i("commentCreateSubscribe", "Subscription completed")
        );
    }

    private void commentDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Comment.class),
                onEstablished -> Log.i("OnDeleteCommentSubscribe", "Subscription established"),
                onUpdated -> {
                    Amplify.API.query(
                            ModelQuery.get(Task.class, getTaskID()),
                            getTaskRes -> {
                                task = getTaskRes.getData();

                                runOnUiThread(() -> {
                                    commentList.clear();
                                    commentList.addAll(task.getComments());
                                    commentListAdapter.notifyDataSetChanged();
                                });
                            },
                            error -> Log.e(TAG, error.toString())
                    );
                },
                onFailure -> Log.e("OnDeleteCommentSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteCommentSubscribe", "Subscription completed")
        );
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BacklogEditTaskActivity.this);
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