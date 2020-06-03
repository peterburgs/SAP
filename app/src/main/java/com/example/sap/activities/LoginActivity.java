package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.example.sap.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tv_signup = findViewById(R.id.tv_signup);
        TextView tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        Button btn_login = findViewById(R.id.btn_login);

        // Create loading dialog
        loadingDialog = new LoadingDialog(LoginActivity.this);

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener((v) -> {
            // Get username, password
            String username = ((EditText) findViewById(R.id.edt_username)).getText().toString();
            String password = ((EditText) findViewById(R.id.edt_password)).getText().toString();

            login(username, password);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("SignUpSuccessMsg") != null) {
                makeToast(toast, getIntent().getExtras().getString("SignUpSuccessMsg"));
            }
            if (getIntent().getExtras().getString("SetPasswordSuccessMsg") != null) {
                makeToast(toast, getIntent().getExtras().getString("SetPasswordSuccessMsg"));
            }
        }
    }

    private void login(String username, String password) {
        // Open loading dialog
        loadingDialog.startLoadingDialog();

        // Send sign in request to AWS
        Amplify.Auth.signIn(username, password,
                result -> {
                    loadingDialog.dismissDialog();
                    //Navigate to project dashboard activity
                    Intent intent = new Intent(LoginActivity.this, ProjectListActivity.class);
                    startActivity(intent);
                },
                error -> {
                    loadingDialog.dismissDialog();
                    Log.e(TAG, "Error", error);
                    runOnUiThread(() -> makeAlert(error.getCause().toString()));
                }
        );

//        AWSMobileClient.getInstance().signIn(username, password, null, new Callback<SignInResult>() {
//            @Override
//            public void onResult(SignInResult result) {
//                loadingDialog.dismissDialog();
//                //Navigate to project dashboard activity
//                Intent intent = new Intent(LoginActivity.this, ProjectDashboardActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                loadingDialog.dismissDialog();
//                Log.e(TAG, "Error", e);
//                runOnUiThread(() -> makeAlert(e.getMessage().split("\\(")[0]));
//            }
//        });
    }

    private void makeToast(Toast toast, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
