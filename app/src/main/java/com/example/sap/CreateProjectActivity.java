package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;

public class CreateProjectActivity extends AppCompatActivity {

    private static final String TAG = CreateProjectActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    EditText edt_projectName;
    EditText edt_projectKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        edt_projectName = findViewById(R.id.edt_projectName);
        edt_projectKey = findViewById(R.id.edt_projectKey);

        loadingDialog = new LoadingDialog(this);
    }
}