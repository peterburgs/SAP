package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sap.R;

public class CreateTaskActivity extends AppCompatActivity {
    EditText edtSummary;
    EditText edtDescription;
    Button btnCreateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        edtSummary = findViewById(R.id.edtSummary);
        edtDescription = findViewById(R.id.edtDescription);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateTaskActivity.this, "Task is created!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}