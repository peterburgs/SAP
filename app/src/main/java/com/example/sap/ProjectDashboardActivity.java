package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;

public class ProjectDashboardActivity extends AppCompatActivity {

    String s1[], s2[];
    int img[] = {};
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);
    }

    private void addProject() {

    }


}
