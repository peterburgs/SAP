package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.sap.adapters.CommentListAdapter;
import com.google.android.material.appbar.MaterialToolbar;

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

import com.example.sap.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SprintEditTaskActivity extends AppCompatActivity {

    private static final String TAG = SprintEditTaskActivity.class.getSimpleName();
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
    Spinner spnStatus;
    Spinner spnAssignee;
    Spinner spnSprint;
    ArrayAdapter<User> spnAssigneeAdapter;
    ArrayAdapter<Sprint> spnSprintAdapter;
    ArrayAdapter<TaskStatus> spnStatusAdapter;
    ArrayList<User> assigneeList;
    ArrayList<Sprint> sprintList;
    ArrayList<TaskStatus> statusList;
    Sprint previousSelectedSprint;
    boolean isInitializeSprintSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_edit_task);

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

        statusList = new ArrayList<>();
        statusList.add(TaskStatus.TODO);
        statusList.add(TaskStatus.IN_PROGRESS);
        statusList.add(TaskStatus.DONE);
        // Spinner
        spnStatus = findViewById(R.id.spnStatus);
        spnStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spnStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(spnStatusAdapter);

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

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SprintEditTaskActivity.this)
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SprintEditTaskActivity.this);
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
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SprintEditTaskActivity.this);
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
                                                    Toast.makeText(SprintEditTaskActivity.this, "Delete comment successfully", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(SprintEditTaskActivity.this);
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

                                    assigneeList.clear();
                                    for (ProjectParticipant member : project.getMembers()) {
                                        assigneeList.add(member.getMember());
                                    }
                                    spnAssigneeAdapter.notifyDataSetChanged();
                                    spnAssignee.setSelection(assigneeList.indexOf(task.getAssignee()));

                                    sprintList.clear();
                                    for(Sprint sprint : project.getSprints()) {
                                        if(!sprint.getIsBacklog()) {
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
                                    spnStatus.setSelection(statusList.indexOf(task.getStatus()));

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
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, task.getProject().getId()),
                getProjectRes -> {

                    User selectedAssignee = (User) spnAssignee.getSelectedItem();
                    Sprint selectedSprint = (Sprint) spnSprint.getSelectedItem();
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
                            .status((TaskStatus)spnStatus.getSelectedItem())
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

    public void onMoveToBacklog(View view) {
        loadingDialog.startLoadingDialog();
        // Get Project
        Amplify.API.query(
                ModelQuery.get(Project.class, task.getProject().getId()),
                getProjectRes -> {

                    User selectedAssignee = (User) spnAssignee.getSelectedItem();
                    Sprint selectedSprint = null;
                    for(Sprint sprint: getProjectRes.getData().getSprints()) {
                        if(sprint.getIsBacklog()) {
                            selectedSprint = sprint;
                        }
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
                            .status((TaskStatus)spnStatus.getSelectedItem())
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

    private void commentMutation(String content) {
        loadingDialog.startLoadingDialog();
        if(content.equals("")) {
            makeAlert("Please type something!");
            return;
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SprintEditTaskActivity.this);
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