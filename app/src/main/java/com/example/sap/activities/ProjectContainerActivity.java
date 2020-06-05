package com.example.sap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStatus;
import com.example.sap.R;
import com.example.sap.adapters.PageAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ProjectContainerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private com.google.android.material.tabs.TabItem titToDo, titInProgress, titDone, titBacklog;
    private BadgeDrawable toDoBadge, inProgressBadge, doneBadge, backlogBadge;
    public PageAdapter pagerAdapter;
    private Handler mHandler;

    com.getbase.floatingactionbutton.FloatingActionButton fabProject;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;
    com.getbase.floatingactionbutton.FloatingActionButton fabSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_container);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        titToDo = findViewById(R.id.titToDo);
        titInProgress = findViewById(R.id.titInProgress);
        titDone = findViewById(R.id.titDone);
        titBacklog = findViewById(R.id.titBacklog);
        fabProject = findViewById(R.id.fabProject);
        fabAccount = findViewById(R.id.fabAccount);
        fabSetting = findViewById(R.id.fabSetting);

        fabProject.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProjectListActivity.class);
            startActivity(intent);
        });
        fabAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(intent);
        });
        fabSetting.setOnClickListener(v -> {
            Toast.makeText(this, "Setting Selected", Toast.LENGTH_SHORT).show();
        });

        mHandler = new Handler(Looper.getMainLooper());

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        toDoBadge = tabLayout.getTabAt(0).getOrCreateBadge();
        inProgressBadge = tabLayout.getTabAt(1).getOrCreateBadge();
        doneBadge = tabLayout.getTabAt(2).getOrCreateBadge();
        backlogBadge = tabLayout.getTabAt(3).getOrCreateBadge();
        toDoBadge.setVisible(true);
        inProgressBadge.setVisible(true);
        doneBadge.setVisible(true);
        backlogBadge.setVisible(true);

        setBadgeNumber();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setBadgeNumber() {
        // Get project by id
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {
                    Sprint activatedSprint = null;
                    Sprint backlog = null;

                    for(Sprint sprint: getProjectRes.getData().getSprints()) {
                        if(sprint.getIsStarted() != null && sprint.getIsStarted()) {
                            activatedSprint = sprint;
                        } else if(sprint.getIsBacklog()) {
                            backlog = sprint;
                        }
                    }

                    if(activatedSprint != null) {
                        // Get tasks of the activated sprint
                        Amplify.API.query(
                                ModelQuery.get(Sprint.class, activatedSprint.getId()),
                                getSprintRes -> {
                                    int todo = 0;
                                    int inProgress = 0;
                                    int done = 0;
                                    for(Task task : getSprintRes.getData().getTasks()) {
                                        if(task.getStatus().equals(TaskStatus.TODO)) {
                                            todo++;
                                        }
                                        if(task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                                            inProgress++;
                                        }
                                        if(task.getStatus().equals(TaskStatus.DONE)) {
                                            done++;
                                        }
                                    }
                                    int finalTodo = todo;
                                    int finalInProgress = inProgress;
                                    int finalDone = done;
                                    mHandler.post(() -> {
                                        toDoBadge.setNumber(finalTodo);
                                        inProgressBadge.setNumber(finalInProgress);
                                        doneBadge.setNumber(finalDone);
                                    });
                                },
                                error -> Log.e("GetSprintError", error.toString())
                        );
                    }

                    if(backlog != null) {
                        // Get tasks of the backlog
                        Amplify.API.query(
                                ModelQuery.get(Sprint.class, backlog.getId()),
                                getBacklogRes -> {
                                    backlogBadge.setNumber(getBacklogRes.getData().getTasks().size());
                                },
                                error -> Log.e("GetSprintError", error.toString())
                        );
                    }
                },
                error -> {
                    Log.e("Error", error.toString());
                }
        );
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