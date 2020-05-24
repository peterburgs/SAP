package com.example.sap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;

public class ConfirmSignupActivity extends AppCompatActivity {

    Button btn_confirm;
    private static final String TAG = ConfirmSignupActivity.class.getSimpleName();
    private LoadingDialog loadingDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_signup);

        TextView tv_message = findViewById(R.id.tv_message);
        tv_message.setText(getIntent().getExtras().getString("msg"));

        TextView tv_resendCode = findViewById(R.id.tv_resendCode);
        Handler handler =new Handler();
        //Show re-send code text view after 10 seconds
        handler.postDelayed(() -> tv_resendCode.setVisibility(View.VISIBLE), 10000);

        //Create loading dialog
        loadingDialog= new LoadingDialog(ConfirmSignupActivity.this);

        //Re-send code event
        tv_resendCode.setOnClickListener((v) -> {
            reSendCode(getIntent().getExtras().getString("username"));
        });

        btn_confirm = findViewById(R.id.btn_confirm);
        // User clicks confirm button
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get verification code
                String code = ((EditText) findViewById(R.id.edt_verificationCode)).getText().toString();
                confirm(getIntent().getExtras().getString("username"), code);
            }
        });
    }

    private void confirm(String username, String code) {
        //Open loading dialog
        loadingDialog.startLoadingDialog();

        //Send confirm request to AWS
        AWSMobileClient.getInstance().confirmSignUp(username, code, new Callback<SignUpResult>() {
            @Override
            public void onResult(SignUpResult result) {
                //Navigate to login activity
                Intent signInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                signInActivityIntent.putExtra("SignUpSuccessMsg", "Sign up successfully");
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

    private void reSendCode(String username) {
        //Open loading dialog
        loadingDialog.startLoadingDialog();

        //Send re-send code request to AWS
        AWSMobileClient.getInstance().resendSignUp(username, new Callback<SignUpResult>() {
            @Override
            public void onResult(SignUpResult result) {
                loadingDialog.dismissDialog();
                makeToast(toast, "We have sent you the code again. Check your email at " + result.getUserCodeDeliveryDetails().getDestination());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmSignupActivity.this);
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
