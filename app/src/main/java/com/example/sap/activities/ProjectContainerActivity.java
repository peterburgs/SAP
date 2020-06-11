package com.example.sap.activities;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.adapters.PageAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static com.example.sap.app.App.CHANNEL_ID;

public class ProjectContainerActivity extends AppCompatActivity {

    private TabLayout tloStatus;
    private ViewPager viewPager;
    private BadgeDrawable toDoBadge, inProgressBadge, doneBadge, backlogBadge;
    public PageAdapter pagerAdapter;
    private Handler mHandler;
    private NotificationManagerCompat notificationManagerCompat;
    private ArrayList<Task> todoTasks;
    private ArrayList<Task> inProgressTasks;
    private ArrayList<Task> doneTasks;
    private ArrayList<Task> backlogTasks;
    private ArrayList<Sprint> activeSprint;
    private ArrayList<Sprint> backlog;
    private LoadingDialog loadingDialog;
    private com.google.android.material.appbar.MaterialToolbar topAppbar;

    com.getbase.floatingactionbutton.FloatingActionButton fabProjectList;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;
    com.getbase.floatingactionbutton.FloatingActionButton fabSetting;
    com.getbase.floatingactionbutton.FloatingActionButton fabSprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_container);

        tloStatus = findViewById(R.id.tloStatus);
        viewPager = findViewById(R.id.vpgStatus);
        fabProjectList = findViewById(R.id.fabProject);
        fabAccount = findViewById(R.id.fabAccount);
        fabSetting = findViewById(R.id.fabSetting);
        fabSprint = findViewById(R.id.fabSprint);
        topAppbar = findViewById(R.id.topAppBar);

        fabProjectList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProjectListActivity.class);
            startActivity(intent);
        });
        fabAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(intent);
        });
        fabSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            intent.putExtra("PROJECT_ID", getProjectID());
            startActivity(intent);
        });
        fabSprint.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SprintContainerActivity.class);
            intent.putExtra("PROJECT_ID", getProjectID());
            startActivity(intent);
        });

        mHandler = new Handler(Looper.getMainLooper());

        loadingDialog = new LoadingDialog(this);

        // Handle PageAdapter
        todoTasks = new ArrayList<>();
        inProgressTasks = new ArrayList<>();
        doneTasks = new ArrayList<>();
        backlogTasks = new ArrayList<>();
        backlog = new ArrayList<>();
        activeSprint = new ArrayList<>();
        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tloStatus.getTabCount(), todoTasks, inProgressTasks, doneTasks, activeSprint, backlogTasks, backlog);
        viewPager.setAdapter(pagerAdapter);

        toDoBadge = tloStatus.getTabAt(0).getOrCreateBadge();
        inProgressBadge = tloStatus.getTabAt(1).getOrCreateBadge();
        doneBadge = tloStatus.getTabAt(2).getOrCreateBadge();
        backlogBadge = tloStatus.getTabAt(3).getOrCreateBadge();
        toDoBadge.setVisible(true);
        inProgressBadge.setVisible(true);
        doneBadge.setVisible(true);
        backlogBadge.setVisible(true);
        toDoBadge.setNumber(0);
        inProgressBadge.setNumber(0);
        doneBadge.setNumber(0);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        query();
        taskCreateSubscribe();
        taskUpdateSubscribe();
        taskDeleteSubscribe();
        sprintUpdateSubscribe();

        tloStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 3) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tloStatus));
    }

    private void query() {
        // Get project by id
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {
                    activeSprint.clear();
                    backlog.clear();
                    for (Sprint sprint : getProjectRes.getData().getSprints()) {
                        if (sprint != null && sprint.getIsStarted() != null && sprint.getIsStarted()) {
                            activeSprint.add(sprint);
                        } else if (sprint.getIsBacklog()) {
                            backlog.add(sprint);
                        }
                    }

                    backlogTasks.clear();
                    todoTasks.clear();
                    inProgressTasks.clear();
                    doneTasks.clear();

                    // Get tasks of the backlog
                    Amplify.API.query(
                            ModelQuery.get(Sprint.class, backlog.get(0).getId()),
                            getBacklogRes -> {
                                backlogTasks.addAll(getBacklogRes.getData().getTasks());

                                if (!activeSprint.isEmpty()) {
                                    // Get tasks of the active sprint
                                    Amplify.API.query(
                                            ModelQuery.get(Sprint.class, activeSprint.get(0).getId()),
                                            getSprintRes -> {
                                                for (Task task : getSprintRes.getData().getTasks()) {
                                                    if (task.getStatus().equals(TaskStatus.TODO)) {
                                                        todoTasks.add(task);
                                                    }
                                                    if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                                                        inProgressTasks.add(task);
                                                    }
                                                    if (task.getStatus().equals(TaskStatus.DONE)) {
                                                        doneTasks.add(task);
                                                    }
                                                }
                                                mHandler.post(() -> {
                                                    toDoBadge.setNumber(todoTasks.size());
                                                    inProgressBadge.setNumber(inProgressTasks.size());
                                                    doneBadge.setNumber(doneTasks.size());
                                                    topAppbar.setTitle(getSprintRes.getData().getProject().getKey() + " Board");
                                                    backlogBadge.setNumber(getBacklogRes.getData().getTasks().size());

                                                    pagerAdapter.notifyDataSetChanged();

                                                });
                                            },
                                            error -> Log.e("GetSprintError", error.toString())
                                    );
                                } else {
                                    mHandler.post(() -> {
                                        toDoBadge.setNumber(0);
                                        inProgressBadge.setNumber(0);
                                        doneBadge.setNumber(0);
                                        topAppbar.setTitle(getBacklogRes.getData().getProject().getKey() + " Board");
                                        backlogBadge.setNumber(getBacklogRes.getData().getTasks().size());

                                        pagerAdapter.notifyDataSetChanged();
                                    });
                                }
                            },
                            error -> Log.e("GetSprintError", error.toString())
                    );
                },
                error -> Log.e("GetSprintError", error.toString())
        );
    }

    private void taskCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Task.class),
                onEstablished -> Log.i("OnCreateTaskSubscribe", "Subscription established"),
                onCreated -> {
                    showNotification("SAP", onCreated.getData().getName() + " has been created in backlog");
                    query();
                },
                onFailure -> Log.e("OnCreateTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnCreateTaskSubscribe", "Subscription completed")
        );
    }

    private void taskUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Task.class),
                onEstablished -> Log.i("OnUpdateTaskSubscribe", "Subscription established"),
                onUpdated -> {
                    showNotification("SAP", onUpdated.getData().getName() + " has been updated");
                    query();
                },
                onFailure -> Log.e("OnUpdateTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateTaskSubscribe", "Subscription completed")
        );
    }

    private void taskDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Task.class),
                onEstablished -> Log.i("OnDeleteTaskSubscribe", "Subscription established"),
                onDeleted -> {
                    showNotification("SAP", onDeleted.getData().getName() + " has been deleted");
                    query();
                },
                onFailure -> Log.e("OnDeleteTaskSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteTaskSubscribe", "Subscription completed")
        );
    }

    private void sprintUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Sprint.class),
                onEstablished -> Log.i("OnUpdateSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    activeSprint.clear();
                    backlog.clear();
                    // Get project by id
                    Amplify.API.query(
                            ModelQuery.get(Project.class, getProjectID()),
                            getProjectRes -> {
                                for (Sprint sprint : getProjectRes.getData().getSprints()) {
                                    if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                        activeSprint.add(sprint);
                                    } else if (sprint.getIsBacklog()) {
                                        backlog.add(sprint);
                                    }
                                }
                                query();
                            },
                            error -> {
                                Log.e("Error", error.toString());
                            }
                    );
                },
                onFailure -> Log.e("OnUpdateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateSprintSubscribe", "Subscription completed")
        );
    }

    private void showNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bookmark_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManagerCompat.notify(1, notification);
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
}