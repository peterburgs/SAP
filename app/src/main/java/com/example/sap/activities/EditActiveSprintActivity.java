package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sap.R;

public class EditActiveSprintActivity extends AppCompatActivity {
    com.google.android.material.textfield.TextInputLayout edtEndDateLayout;
    com.google.android.material.textfield.TextInputEditText edtEndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_active_sprint);
        
        edtEndDate=findViewById(R.id.edtEndDate);
        edtEndDateLayout=findViewById(R.id.edtEndDateLayout);
        edtEndDateLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActiveSprintActivity.this, "Calendar Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        
        
    }
}