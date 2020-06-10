package com.example.sap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sap.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.TimeZone;

public class EditActiveSprintActivity extends AppCompatActivity {


    com.google.android.material.textfield.TextInputLayout edtEndDateLayout;
    com.google.android.material.textfield.TextInputEditText edtEndDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_active_sprint);


        //Calendar
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        calendar.clear();
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);

        //Calendar Constraint
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        CalendarConstraints.DateValidator dateValidator = DateValidatorPointForward.now();
        constraintBuilder.setValidator(dateValidator);
        //DatePicker
        MaterialDatePicker.Builder<Pair<Long, Long>> dateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        dateBuilder.setTitleText("Choose Time Range For Sprint");
        dateBuilder.setCalendarConstraints(constraintBuilder.build());
        dateBuilder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

        final MaterialDatePicker materialDatePicker = dateBuilder.build();

        edtEndDate = findViewById(R.id.edtEndDate);
        edtEndDateLayout = findViewById(R.id.edtEndDateLayout);
        edtEndDateLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Toast.makeText(EditActiveSprintActivity.this, materialDatePicker.getHeaderText(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }
}