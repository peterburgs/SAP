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

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.sap.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    Toast toast;
    TextView tv_signin;
    Button btn_signup;
    private static final String TAG = SignupActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Define tags from XML to Java
        tv_signin = findViewById(R.id.tv_signin);
        btn_signup = findViewById(R.id.btn_signup);

        // Create loading dialog
        loadingDialog = new LoadingDialog(SignupActivity.this);

        //User Touches "Sign in" to go back to "LogIn Activity"
        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        //User fills out the form and touch "Sign Up"
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username, password, and email
                String username = ((EditText) findViewById(R.id.edt_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.edt_password)).getText().toString();
                String confirmPassword = ((EditText) findViewById(R.id.edt_confirmPassword)).getText().toString();
                String email = ((EditText) findViewById(R.id.edt_email)).getText().toString();

                signUp(username, password, confirmPassword, email);
            }
        });
    }

    private void signUp(final String username, String password, String confirmPassword, String email) {
        if (validateInput(email, password, confirmPassword)) {
            // Open loading dialog
            loadingDialog.startLoadingDialog();

            // Send sign-up request to AWS
            Amplify.Auth.signUp(username, password, AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
                    result -> {
                        Intent confirmSignUpActivityIntent = new Intent(getApplicationContext(), ConfirmSignupActivity.class);

                        String destination = result.getNextStep().getCodeDeliveryDetails().getDestination();
                        confirmSignUpActivityIntent.putExtra("msg", "The verification code has been sent to " + destination);
                        confirmSignUpActivityIntent.putExtra("username", username);
                        loadingDialog.dismissDialog();
                        startActivity(confirmSignUpActivityIntent);
                    },
                    error -> {
                        loadingDialog.dismissDialog();
                        Log.e(TAG, "Error", error);
                        runOnUiThread(() -> makeAlert(error.getCause().toString()));
                    }
            );
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                makeToast(toast, "Invalid email format");
                return false;
            }
            return true;
        } else {
            makeToast(toast, "Confirm password does not match");
            return false;
        }
    }

    private void makeToast(Toast toast, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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
