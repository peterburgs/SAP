package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.adapters.PageAdapter;
import com.example.sap.adapters.SprintPageAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class SprintContainerActivity extends AppCompatActivity {

    private TabLayout tloSprint;
    private ViewPager vpgSprint;
    private com.google.android.material.tabs.TabItem titActive, titFuture, titCompleted;
    private BadgeDrawable activeBadge, futureBadge, completedBadge;
    private SprintPageAdapter sprintPageAdapter;
    private Handler mHandler;
    private NotificationManagerCompat notificationManagerCompat;
    private ArrayList<Sprint> activeSprints;
    private ArrayList<Sprint> futureSprints;
    private ArrayList<Sprint> completedSprints;
    private LoadingDialog loadingDialog;

    com.getbase.floatingactionbutton.FloatingActionButton fabProject;
    com.getbase.floatingactionbutton.FloatingActionButton fabAccount;
    com.getbase.floatingactionbutton.FloatingActionButton fabSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_container);

        //FindView
        tloSprint = findViewById(R.id.tloSprint);
        vpgSprint = findViewById(R.id.vpgSprint);
        titActive = findViewById(R.id.titActive);
        titFuture = findViewById(R.id.titFuture);
        titCompleted = findViewById(R.id.titCompleted);
        fabProject = findViewById(R.id.fabProject);
        fabAccount = findViewById(R.id.fabAccount);
        fabSetting = findViewById(R.id.fabSetting);


        //todo: Add Adapter to initializeSprintPageAdapter


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

        //todo: Backend handle Subscribes

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

        //todo: Hard time for backend
    }
}