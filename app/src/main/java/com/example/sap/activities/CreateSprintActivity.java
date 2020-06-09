package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.appbar.MaterialToolbar;

public class CreateSprintActivity extends AppCompatActivity {
    com.google.android.material.button.MaterialButton btnCreateSprint;
    com.google.android.material.textfield.TextInputEditText edtSprintGoal, edtSprintName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sprint);

        //Find UI Components
        btnCreateSprint = findViewById(R.id.btnCreateSprint);
        edtSprintGoal = findViewById(R.id.edtSprintGoal);
        edtSprintName = findViewById(R.id.edtSprintName);


    }

    public void onCreateSprintClick(View view) {
        Toast.makeText(this, "New Sprint Created!", Toast.LENGTH_SHORT).show();
    }

}