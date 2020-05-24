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

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btn_sendCode;
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Create loading dialog
        loadingDialog = new LoadingDialog(ForgotPasswordActivity.this);

        btn_sendCode = findViewById(R.id.btn_sendCode);
        btn_sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username
                String username = ((EditText)findViewById(R.id.edt_username)).getText().toString();

                sendCode(username);
            }
        });
    }

    private void sendCode(String username) {
        // Open loading dialog
        loadingDialog.startLoadingDialog();

        //Send forgot password request to AWS
        AWSMobileClient.getInstance().forgotPassword(username, new Callback<ForgotPasswordResult>() {
            @Override
            public void onResult(ForgotPasswordResult result) {
                loadingDialog.dismissDialog();

                //Navigate to reset password activity
                Intent resetPasswordActivityIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(resetPasswordActivityIntent);
            }

            @Override
            public void onError(Exception e) {
                loadingDialog.dismissDialog();
                Log.e(TAG,"Error", e);
                runOnUiThread(() -> makeAlert(e.getMessage().split("\\(")[0]));
            }
        });
    }

    private void makeToast(Toast toast, String message) {
        if(toast!=null){
            toast.cancel();
        }
        toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeAlert(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
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
