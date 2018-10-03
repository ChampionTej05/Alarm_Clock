package com.example.champion.map_assignment_alarm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Date_Picker_practice extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener mDateListener;
    TimePickerDialog.OnTimeSetListener mTimeSetListener;
    Button showdialog;
    TextView showText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date__picker_practice);

        showdialog=findViewById(R.id.ShowDialogButton);
        showText=findViewById(R.id.Showtext);
        showdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to set the current date in Dialog

                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

//                DatePickerDialog datePickerDialog=new DatePickerDialog(Date_Picker_practice.this,
//                        R.style.Theme_AppCompat_DayNight_Dialog_Alert,mDateListener,year,month,day);
//
//
//                datePickerDialog.show();
              TimePickerDialog timePickerDialog=new TimePickerDialog(Date_Picker_practice.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert,
                      mTimeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
              timePickerDialog.show();
            }
        });

//           mDateListener=new DatePickerDialog.OnDateSetListener() {
//               @Override
//               public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                //Month starts from 0, hence Jan=0 and Dec=11
//                   month=month+1;
//                    showText.setText("Month: "+String.valueOf(month));
//
//
//               }
//           };

           mTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
               @Override
               public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    showText.setText("TIme: "+i+" / "+i1);
               }
           };


    }
}
