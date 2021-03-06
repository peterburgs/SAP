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
import android.widget.AdapterView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
    EditText edtEstimatedTime;
    com.google.android.material.textfield.TextInputLayout edtCommentLayout;
    com.google.android.material.textfield.TextInputEditText edtComment;
    Spinner spnAssignee;
    ArrayAdapter<User> spnAssigneeAdapter;
    ArrayList<User> assigneeList;
    Spinner spnSprint;
    ArrayAdapter<Sprint> spnSprintAdapter;
    ArrayList<Sprint> sprintList;
    Sprint previousSelectedSprint;
    boolean isInitializeSprintSelection = true;

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
        edtEstimatedTime = findViewById(R.id.edtEstimatedTime);
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
                                if (!getCurrentUserRes.getData().equals(commentList.get(position).getAuthor())) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(BacklogEditTaskActivity.this);
                                    builder2.setMessage("You are not allowed to delete this comment");
                                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    runOnUiThread(() -> {
                                        loadingDialog.dismissDialog();
                                        AlertDialog dialog2 = builder2.create();
                                        dialog2.show();
                                    });
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

        spnSprint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isInitializeSprintSelection) {
                    if(sprintList.get(position).getIsStarted() != null && sprintList.get(position).getIsStarted()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BacklogEditTaskActivity.this);
                        builder.setMessage("Sprint scope will be affected by this action");
                        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spnSprint.setSelection(sprintList.indexOf(previousSelectedSprint));
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        previousSelectedSprint = sprintList.get(position);
                    }
                } else {
                    isInitializeSprintSelection = false;
                    previousSelectedSprint = sprintList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                                    edtEstimatedTime.setText(task.getEstimatedTime() == 0f ? "" : String.valueOf(task.getEstimatedTime()));

                                    // Data for assignee spinner
                                    assigneeList.clear();
                                    for (ProjectParticipant member : project.getMembers()) {
                                        assigneeList.add(member.getMember());
                                    }
                                    spnAssigneeAdapter.notifyDataSetChanged();
                                    spnAssignee.setSelection(assigneeList.indexOf(task.getAssignee()));

                                    // Data for sprint spinner
                                    sprintList.clear();
                                    for(Sprint sprint : project.getSprints()) {
                                        if(sprint.getIsCompleted() == null || !sprint.getIsCompleted()) {
                                            sprintList.add(sprint);
                                        }
                                    }
                                    spnSprintAdapter.notifyDataSetChanged();
                                    spnSprint.setSelection(sprintList.indexOf(task.getSprint()));

                                    commentList.clear();
                                    commentList.addAll(task.getComments());
                                    Collections.sort(commentList, new Comparator<Comment>() {
                                        @Override
                                        public int compare(Comment o1, Comment o2) {
                                            return o1.getCreatedAt().toDate().compareTo(o2.getCreatedAt().toDate());
                                        }
                                    });
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
        if(content.equals("")) {
            makeAlert("Please type something!");
            loadingDialog.dismissDialog();
            return;
        }

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
        // Validate estimated time
        if (!edtEstimatedTime.getText().toString().equals("")) {
            try {
                if(Float.parseFloat(edtEstimatedTime.getText().toString()) <= 0) {
                    makeAlert("Estimated time must be greater than 0!");
                    return;
                }
            } catch (NumberFormatException e) {
                makeAlert("Estimated time is incorrect format!");
                return;
            }
        }

        loadingDialog.startLoadingDialog();
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, task.getProject().getId()),
                getProjectRes -> {

                    User selectedAssignee = (User) spnAssignee.getSelectedItem();
                    Sprint selectedSprint = (Sprint) spnSprint.getSelectedItem();
                    TaskStatus taskStatus = null;
                    if(!selectedSprint.getIsBacklog()) {
                        taskStatus = TaskStatus.TODO;
                    }

                    // Update Task
                    Task taskMutation = Task.builder()
                            .name(task.getName())
                            .summary(edtSummary.getText().toString())
                            .project(task.getProject())
                            .assignee(selectedAssignee)
                            .sprint(selectedSprint)
                            .description(edtDescription.getText().toString())
                            .label(edtLabel.getText().toString())
                            .id(task.getId())
                            .status(taskStatus)
                            .estimatedTime(edtEstimatedTime.getText().toString().equals("") ? 0 : Float.parseFloat(edtEstimatedTime.getText().toString()))
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
                                    Collections.sort(commentList, new Comparator<Comment>() {
                                        @Override
                                        public int compare(Comment o1, Comment o2) {
                                            return o1.getCreatedAt().toDate().compareTo(o2.getCreatedAt().toDate());
                                        }
                                    });
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
                                    Collections.sort(commentList, new Comparator<Comment>() {
                                        @Override
                                        public int compare(Comment o1, Comment o2) {
                                            return o1.getCreatedAt().toDate().compareTo(o2.getCreatedAt().toDate());
                                        }
                                    });
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