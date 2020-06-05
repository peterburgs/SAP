package com.example.sap.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.adapters.PageAdapter;
import com.example.sap.fragments.ToDoFragment;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

public class EditTaskActivity extends AppCompatActivity {

    private static final String TAG = EditTaskActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    MaterialToolbar topAppBar;
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        task = null;

        taskQuery();


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
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EditTaskActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.show();

                }
                if (item.getOrder() == 2) {
                    Toast.makeText(EditTaskActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
                return true;
            }
        });

    }

    private void taskQuery() {
        Amplify.API.query(
                ModelQuery.get(Task.class, getTaskID()),
                response -> {
                    task = response.getData();
                },
                error -> Log.e(TAG, error.toString())
        );
    }

    private String getTaskID() {
        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("TASK_ID");
        }
        return newString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}