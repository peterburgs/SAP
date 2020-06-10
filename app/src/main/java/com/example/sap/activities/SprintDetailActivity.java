package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sap.R;

public class SprintDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_detail);
        if(getLabel().equals("ACTIVE")) {
            Toast.makeText(this, "This is sprint detail for active", Toast.LENGTH_SHORT).show();
        } else if(getLabel().equals("FUTURE")) {
            Toast.makeText(this, "This is sprint detail for future", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This is sprint detail for completed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLabel() {
        String str = null;
        if(getIntent().getExtras() != null) {
            str = getIntent().getExtras().getString("LABEL");
        }

        return str;
    }
}