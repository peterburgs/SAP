package com.example.sap.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sap.fragments.ToDoFragment;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditTaskActivity extends AppCompatActivity {


    MaterialToolbar topAppBar;
    //MaterialToolbar tbSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 1) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditTaskActivity.this)
                            .setIcon(R.drawable.ic_alert)
                            .setTitle("Remove Task")
                            .setMessage("Do you want to remove this task?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(EditTaskActivity.this, "Remove Successfully!", Toast.LENGTH_SHORT).show();
                                    //todo: Handle Remove Task

                                    //
                                    Intent intent = new Intent(EditTaskActivity.this, ProjectContainerActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EditTaskActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.show();

                }
                if (item.getOrder() == 2) {
                    Toast.makeText(EditTaskActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    //todo: Handle Save Task

                    //
                    Intent intent = new Intent(EditTaskActivity.this, ProjectContainerActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

    }


}