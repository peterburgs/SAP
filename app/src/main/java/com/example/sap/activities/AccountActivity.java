package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = AccountActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    TextView tv_username;
    TextView tv_email;
    ImageView imv_avatar;

    com.getbase.floatingactionbutton.FloatingActionButton fabProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tv_username = findViewById(R.id.tv_username);
        tv_email = findViewById(R.id.tv_email);
        imv_avatar = findViewById(R.id.imv_avatar);
        fabProject = findViewById(R.id.fabProject);
        loadingDialog = new LoadingDialog(AccountActivity.this);

        fabProject.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProjectListActivity.class);
            startActivity(intent);
        });

        userQuery();
    }

    public void onLogoutClick(View view) {
        loadingDialog.startLoadingDialog();
        Amplify.Auth.signOut(
                () -> {
                    loadingDialog.dismissDialog();
                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                    startActivity(intent);
                },
                error -> Log.e(TAG, "Error", error)
        );
    }

    public void onResetPassword(View view) {
        loadingDialog.startLoadingDialog();
        Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intent);
        loadingDialog.dismissDialog();
    }

    private void userQuery() {
        loadingDialog.startLoadingDialog();
        Amplify.API.query(
                ModelQuery.get(User.class, Amplify.Auth.getCurrentUser().getUserId()),
                userQueryRes -> {
                    runOnUiThread(() -> {
                        tv_username.setText(userQueryRes.getData().getUsername());
                        tv_email.setText(userQueryRes.getData().getEmail());
                    });

                    Amplify.Storage.getUrl(
                            userQueryRes.getData().getAvatarKey(),
                            userAvatarUrlRes -> {
                                loadingDialog.dismissDialog();
                                runOnUiThread(() -> Picasso.get().load(userAvatarUrlRes.getUrl().toString()).into(imv_avatar));
                            },
                            error -> Log.e(TAG, "Error", error)
                    );
                },
                error -> Log.e(TAG, "Error", error)
        );
    }
}