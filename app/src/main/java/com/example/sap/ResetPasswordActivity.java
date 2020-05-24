package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.ForgotPasswordResult;

public class ResetPasswordActivity extends AppCompatActivity {
    Button btn_confirm;
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Create loading dialog
        loadingDialog = new LoadingDialog(ResetPasswordActivity.this);

        makeToast(toast,"We have sent the verification code to your email");

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText)findViewById(R.id.edt_password)).getText().toString();
                String confirmPassword = ((EditText)findViewById(R.id.edt_confirmPassword)).getText().toString();
                String code = ((EditText)findViewById(R.id.edt_verificationCode)).getText().toString();

                resetPassword(password, confirmPassword, code);
            }
        });
    }

    private void resetPassword(String password, String confirmPassword, String code) {
        if(!password.equals(confirmPassword)) {
            makeToast(toast,"Confirm password does not match");
        } else {
            loadingDialog.startLoadingDialog();

            AWSMobileClient.getInstance().confirmForgotPassword(password, code, new Callback<ForgotPasswordResult>() {
                @Override
                public void onResult(ForgotPasswordResult result) {
                    //Navigate to sign in activity
                    Intent signInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    signInActivityIntent.putExtra("SetPasswordSuccessMsg", "Set new password successfully");
                    loadingDialog.dismissDialog();
                    startActivity(signInActivityIntent);
                }

                @Override
                public void onError(Exception e) {
                    loadingDialog.dismissDialog();
                    Log.e(TAG,"Error", e);
                    runOnUiThread(() -> makeAlert(e.getMessage().split("\\(")[0]));
                }
            });
        }
    }

    private void makeToast(Toast toast, String message) {
        if(toast!=null){
            toast.cancel();
        }
        toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
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
