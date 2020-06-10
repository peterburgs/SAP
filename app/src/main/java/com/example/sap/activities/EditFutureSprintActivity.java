package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.view.View;

import com.example.sap.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class EditFutureSprintActivity extends AppCompatActivity {


    com.google.android.material.textfield.TextInputLayout edtDurationLayout;
    com.google.android.material.textfield.TextInputEditText edtDuration;

    MaterialDatePicker.Builder<Pair<Long, Long>> dateBuilder;
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_future_sprint);


        edtDuration = findViewById(R.id.edtDuration);
        edtDurationLayout = findViewById(R.id.edtDurationLayout);
        edtDurationLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //DatePicker
                dateBuilder = MaterialDatePicker.Builder.dateRangePicker();
                dateBuilder.setTitleText("Choose Time Range For Sprint");

                dateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        edtDuration.setText(materialDatePicker.getHeaderText());
                        //todo: Define builder inside Query
                    }
                });


            }
        });

    }
}