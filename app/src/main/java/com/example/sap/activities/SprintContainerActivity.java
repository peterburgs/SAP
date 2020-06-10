package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.adapters.PageAdapter;
import com.example.sap.adapters.SprintPageAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class SprintContainerActivity extends AppCompatActivity {

    private TabLayout tloSprint;
    private ViewPager vpgSprint;
    private BadgeDrawable activeBadge, futureBadge, completedBadge;
    private SprintPageAdapter sprintPageAdapter;
    private Handler mHandler;
    private NotificationManagerCompat notificationManagerCompat;
    private ArrayList<Sprint> activeSprints;
    private ArrayList<Sprint> futureSprints;
    private ArrayList<Sprint> completedSprints;
    private LoadingDialog loadingDialog;
    private MaterialToolbar topAppBar;

    com.getbase.floatingactionbutton.FloatingActionButton fabProject;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;
    com.getbase.floatingactionbutton.FloatingActionButton fabSetting;
    com.getbase.floatingactionbutton.FloatingActionButton fabBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_container);

        //FindView
        tloSprint = findViewById(R.id.tloSprint);
        vpgSprint = findViewById(R.id.vpgSprint);
        fabProject = findViewById(R.id.fabProject);
        fabAccount = findViewById(R.id.fabAccount);
        fabSetting = findViewById(R.id.fabSetting);
        fabBoard = findViewById(R.id.fabBoard);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), CreateSprintActivity.class);
                intent.putExtra("PROJECT_ID", getProjectID());
                startActivity(intent);
                return true;
            }
        });
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
        fabBoard.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProjectContainerActivity.class);
            intent.putExtra("PROJECT_ID", getProjectID());
            startActivity(intent);
        });

        mHandler = new Handler(Looper.getMainLooper());

        loadingDialog = new LoadingDialog(this);

        activeSprints = new ArrayList<>();
        futureSprints = new ArrayList<>();
        completedSprints = new ArrayList<>();
        sprintPageAdapter = new SprintPageAdapter(getSupportFragmentManager(), tloSprint.getTabCount(), activeSprints, futureSprints, completedSprints);
        vpgSprint.setAdapter(sprintPageAdapter);

        activeBadge = tloSprint.getTabAt(0).getOrCreateBadge();
        futureBadge = tloSprint.getTabAt(1).getOrCreateBadge();
        completedBadge = tloSprint.getTabAt(2).getOrCreateBadge();

        activeBadge.setVisible(true);
        futureBadge.setVisible(true);
        completedBadge.setVisible(true);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        query();
        sprintCreateSubscribe();
        sprintUpdateSubscribe();
        sprintDeleteSubscribe();

        tloSprint.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpgSprint.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    sprintPageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    sprintPageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2) {
                    sprintPageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpgSprint.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tloSprint));
    }

    private void query() {
        Amplify.API.query(
                ModelQuery.get(Project.class, getProjectID()),
                getProjectRes -> {
                    activeSprints.clear();
                    futureSprints.clear();
                    completedSprints.clear();
                    for (Sprint sprint : getProjectRes.getData().getSprints()) {
                        if (!sprint.getIsBacklog()) {
                            if (sprint.getIsStarted() != null && sprint.getIsStarted()) {
                                activeSprints.add(sprint);
                            } else if (sprint.getIsStarted() != null && sprint.getIsCompleted()) {
                                completedSprints.add(sprint);
                            } else {
                                futureSprints.add(sprint);
                            }
                        }
                    }
                    mHandler.post(() -> {
                        activeBadge.setNumber(activeSprints.size());
                        futureBadge.setNumber(futureSprints.size());
                        completedBadge.setNumber(completedSprints.size());

                        sprintPageAdapter.notifyDataSetChanged();
                    });
                },
                error -> {
                    Log.e("Error", error.toString());
                }
        );
    }

    private void sprintCreateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onCreate(Sprint.class),
                onEstablished -> Log.i("OnCreateSprintSubscribe", "Subscription established"),
                onCreated -> {
                    query();
                },
                onFailure -> Log.e("OnCreateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnCreateSprintSubscribe", "Subscription completed")
        );
    }

    private void sprintUpdateSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onUpdate(Sprint.class),
                onEstablished -> Log.i("OnUpdateSprintSubscribe", "Subscription established"),
                onUpdated -> {
                    query();
                },
                onFailure -> Log.e("OnUpdateSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnUpdateSprintSubscribe", "Subscription completed")
        );
    }

    private void sprintDeleteSubscribe() {
        Amplify.API.subscribe(
                ModelSubscription.onDelete(Sprint.class),
                onEstablished -> Log.i("OnDeleteSprintSubscribe", "Subscription established"),
                onDeleted -> {
                    query();
                },
                onFailure -> Log.e("OnDeleteSprintSubscribe", "Subscription failed", onFailure),
                () -> Log.i("OnDeleteSprintSubscribe", "Subscription completed")
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