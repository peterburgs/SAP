package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Role;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InviteParticipantActivity extends AppCompatActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    EditText edtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_participant);
        edtUsername = findViewById(R.id.edtUsername);
        loadingDialog = new LoadingDialog(this);
    }

    public void onInviteParticipantClick(View view) {
        if (edtUsername.getText().toString().equals("")) {
            makeAlert("username cannot be empty");
            return;
        }
        if(edtUsername.getText().toString().equals(Amplify.Auth.getCurrentUser().getUsername())) {
            makeAlert("You cannot invite yourself");
            return;
        }
        loadingDialog.startLoadingDialog();
        Amplify.API.query(
                ModelQuery.list(User.class, User.USERNAME.eq(edtUsername.getText().toString())),
                getUserListRes -> {
                    List<User> users = StreamSupport.stream(getUserListRes.getData().spliterator(), false).collect(Collectors.toList());
                    if (users.isEmpty()) {
                        runOnUiThread(() -> makeAlert("Your team member has not registered to SAP yet."));
                        loadingDialog.dismissDialog();
                    } else {

                        Amplify.API.query(
                                ModelQuery.get(Project.class, getProjectID()),
                                getProjectRes -> {
                                    // Add User and Project to Project participant
                                    ProjectParticipant projectParticipant = ProjectParticipant.builder()
                                            .role(Role.TEAM_MEMBER)
                                            .project(getProjectRes.getData())
                                            .member(users.get(0))
                                            .build();

                                    Amplify.API.mutate(
                                            ModelMutation.create(projectParticipant),
                                            createProjectParticipantRes -> {
                                                loadingDialog.dismissDialog();
                                                Intent intent = new Intent(InviteParticipantActivity.this, SettingActivity.class);
                                                intent.putExtra("PROJECT_ID", getProjectID());
                                                startActivity(intent);
                                            },
                                            error -> {
                                                loadingDialog.dismissDialog();
                                                Log.e(TAG, "Error", error);
                                                runOnUiThread(() -> makeAlert(error.getCause().toString()));
                                            }
                                    );
                                },
                                error -> {
                                    loadingDialog.dismissDialog();
                                    Log.e(TAG, "Error", error);
                                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                                }
                        );

                    }
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );
    }


    private String getProjectID() {
        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = null;
        } else {
            newString = extras.getString("PROJECT_ID");
        }
        return newString;
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InviteParticipantActivity.this);
        builder.setMessage(content);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}