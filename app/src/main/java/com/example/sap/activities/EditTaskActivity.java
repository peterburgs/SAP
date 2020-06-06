package com.example.sap.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.adapters.CommentListAdapter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    private static final String TAG = EditTaskActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;


    private CommentListAdapter commentListAdapter;
    ArrayList<Comment> commentList;
    RecyclerView rcvCommentList;


    MaterialToolbar topAppBar;
    Task task;
    EditText edtDescription;
    Spinner spnStatus;
    Spinner spnAssignee;
    Spinner spnSprint;
    Spinner spnCommentOption;
    ArrayList<String> assignee = new ArrayList<String>();
    ArrayList<String> sprint = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //commentList = new ArrayList<>();
        // rcvCommentList = findViewById(R.id.rcvComment);
        //rcvCommentList.setHasFixedSize(true);
        //commentListAdapter = new CommentListAdapter(commentList);

        //rcvCommentList.setLayoutManager(new LinearLayoutManager(this));
        //rcvCommentList.setAdapter(commentListAdapter);
        //commentListAdapter.setOnItemClickListener(new CommentListAdapter.OnItemClickListener() {
        // @Override
        //  public void onItemClick(int position) {

        //}
        // });


        //Comment
//        spnCommentOption = findViewById(R.id.spnCommentOption);
//        ArrayAdapter<CharSequence> spnCommentOptionAdapter = ArrayAdapter.createFromResource(this, R.array.commentOption, android.R.layout.simple_spinner_dropdown_item);
//        spnCommentOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnCommentOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Object item = parent.getItemAtPosition(position);
//                //todo:Handle Options: delete || edit
//                if (item.toString().equals("Delete")) {
//                    Toast.makeText(EditTaskActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                }
//                if (item.toString().equals("Edit")) {
//                    Toast.makeText(EditTaskActivity.this, "Edited", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spnCommentOption.setAdapter(spnCommentOptionAdapter);


        edtDescription = findViewById(R.id.edtDescription);
        task = null;

        taskQuery();
        //Hardcode for Assignee
        assignee.add("Peter");
        assignee.add("John");
        assignee.add("Jimmy");

        //Hardcode for Sprint
        sprint.add("Week 1");
        sprint.add("Week 2");
        sprint.add("Week 3");


        spnAssignee = findViewById(R.id.spnAssignee);
        ArrayAdapter<String> spnAssigneeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, assignee);
        spnAssigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnAssignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo: Handle Assignee
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnAssignee.setAdapter(spnAssigneeAdapter);
        //
        //Spinner
        spnStatus = findViewById(R.id.spnTab);
        ArrayAdapter<CharSequence> spnTabAdapter = ArrayAdapter.createFromResource(this, R.array.tabs, android.R.layout.simple_spinner_dropdown_item);
        spnTabAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo:Handle Status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnStatus.setAdapter(spnTabAdapter);
        //Spinner
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

        spnSprint = findViewById(R.id.spnSprint);
        ArrayAdapter<CharSequence> spnSprintAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sprint);
        spnSprintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSprint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                //todo:Handle Status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnSprint.setAdapter(spnSprintAdapter);

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