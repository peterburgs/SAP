package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sap.R;

public class InviteParticipantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_participant);
    }
    public void onInviteParticipant(View view){
        //todo: Backend handles Invite Participant
        Toast.makeText(this, "Invited!", Toast.LENGTH_SHORT).show();
    }
}