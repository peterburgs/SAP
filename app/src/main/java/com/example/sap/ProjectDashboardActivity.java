package com.example.sap;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;

import java.io.File;

public class ProjectDashboardActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    String s1[], s2[];
    int img[] = {R.drawable.project_img1, R.drawable.project_img2, R.drawable.project_img3, R.drawable.project_img4, R.drawable.project_img5};

    RecyclerView rcvProjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);
        rcvProjectList = findViewById(R.id.rcvProjectList);

        s1 = getResources().getStringArray(R.array.Project);
        s2 = getResources().getStringArray(R.array.Key);

        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, s1, s2, img);
        rcvProjectList.setAdapter(projectListAdapter);
        rcvProjectList.setLayoutManager(new LinearLayoutManager(this));
        Log.i("Test", getApplicationContext().getFilesDir().getAbsolutePath());
    }


}
