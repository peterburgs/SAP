package com.example.sap;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

public class ProjectDashboardActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    //Fetch Data Here:
    String s1[] = {"Simple Android Project", "Online Banking", "Internet Security", "Super Mall", "Internet of Things"};
    String s2[] = {"SAP", "OB", "IS", "SM", "IOT"};
    //

    int img[] = {R.drawable.project_img1, R.drawable.project_img2, R.drawable.project_img3, R.drawable.project_img4, R.drawable.project_img5};

    RecyclerView rcvProjectList;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);
        rcvProjectList = findViewById(R.id.rcvProjectList);
        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //todo: Create New Project Here

                return true;
            }
        });
        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, s1, s2, img);
        rcvProjectList.setAdapter(projectListAdapter);
        rcvProjectList.setLayoutManager(new LinearLayoutManager(this));

    }
}
