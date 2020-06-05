package com.example.sap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sap.R;
import com.example.sap.adapters.PageAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ProjectContainerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private com.google.android.material.tabs.TabItem titToDo, titInProgress, titDone, titBacklog;
    public PageAdapter pagerAdapter;

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


        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        BadgeDrawable toDoBadge, inProgressBadge, doneBadge, backlogBadge;
        toDoBadge = tabLayout.getTabAt(0).getOrCreateBadge();
        inProgressBadge = tabLayout.getTabAt(1).getOrCreateBadge();
        doneBadge = tabLayout.getTabAt(2).getOrCreateBadge();
        backlogBadge = tabLayout.getTabAt(3).getOrCreateBadge();
        toDoBadge.setVisible(true);
        inProgressBadge.setVisible(true);
        doneBadge.setVisible(true);
        backlogBadge.setVisible(true);

        //todo: Get Number of ToDo Tasks and pass to  badges
        toDoBadge.setNumber(10);
        inProgressBadge.setNumber(10);
        doneBadge.setNumber(10);
        backlogBadge.setNumber(10);

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
}