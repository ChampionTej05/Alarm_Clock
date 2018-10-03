package com.example.champion.map_assignment_alarm;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    AlarmManager alarm_manager;
    Context context;
    Button alarm_set;
    static Switch alarm_on;
    PendingIntent pending_intent;
    int choose_sound=1;
     Intent my_intent;

     static boolean notificationOn=false;

     AlertDialog.Builder alBuilder;


     int hour=55,minute=90;
     static  String FILTER_ACTION_KEY="Champion";

     static String BROADCAST_SEND="Champion05";

     MyBroadCastReceiver myBroadCastReceiver;
    TimePickerDialog.OnTimeSetListener mTimeSetListener;

    /*
    This class will show the button layout and ask the user to specify the time for the Alarm.
    We will use the Alarm Manager class to schedule the alarm and we will RTC_wakeup service for the scheduling
    so that even though the screen is off we can wakeup the screen and trigger the alarm.

    We will send the broadcast single to the AlarmReceiver class which will ask the service to play the
    sound till the time we press Alarm Off button.

    When we press alarm off button, we will detach the alarm manager and fire an intent to signal service class
    that we want to stop the alarm tone.
     */

    /*
        Please add the necessary string array in Strings.xml file so that we could use spinner.
        Please refer "res" folder for the resources.

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context=this;
        //alarm_timepicker=findViewById(R.id.timePicker);

        alarm_manager=(AlarmManager)getSystemService(ALARM_SERVICE);

        alarm_on=findViewById(R.id.alarm_on);

        alarm_set=findViewById(R.id.timePicker);



        showInstructions();
        setReceiver();
        notificationOn=false;

        // my_intent=new Intent(this.context,RingtonePlayingService.class);

         my_intent=new Intent(this.context,Alarm_BroadCast_Receiver.class);
        my_intent.setAction(BROADCAST_SEND);
        final Calendar calendar=Calendar.getInstance();

        int h=calendar.get(Calendar.HOUR_OF_DAY);
        int m=calendar.get(Calendar.MINUTE);
        String s="";
        String min="";
         min=String.valueOf(m);
        if(m<10){
            min="0"+m;
        }

        if(h>12){
            h=h-12;
            s=h+" : "+min+"PM";
        }
        else{
            s=h+" : "+min+"AM";
        }
        /*Sarthak's addition*/
//        String currentTimeString = new SimpleDateFormat("HH:mm").format(new Date());;
        alarm_set.setText(s);
        /*Sarthak's addition*/

        Spinner spinner =  findViewById(R.id.ringtone_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,

                R.array.whale_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        alarm_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert,
                        mTimeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                alarm_on.setChecked(false);
                timePickerDialog.show();
            }
        });

        mTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hour=i;
                minute=i1;
                alarm_on.setChecked(true);



            }
        };

        alarm_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(alarm_on.isChecked()){
                    if(hour==55 || minute==90){
                        //do nothing
                        Toast.makeText(context, "Please Select Time", Toast.LENGTH_SHORT).show();
                        alarm_on.setChecked(false);
                    }
                    else{

                        calendar.set(Calendar.HOUR_OF_DAY,hour);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,1);

                        // convert the int values to strings

                        String hour_string = String.valueOf(hour);

                        String minute_string = String.valueOf(minute);

                        // convert 24-hour time to 12-hour time

                        if (minute < 10) {
                            minute_string = "0" + String.valueOf(minute);
                        }

                        if (hour > 12) {

                            hour_string = String.valueOf(hour - 12);
                            set_alarm_text(hour_string+" : "+minute_string+"PM");

                        }
                        else{
                            set_alarm_text(hour_string+" : "+minute_string+"AM");

                        }


                        // method that changes the update text


                        my_intent.putExtra("extra","alarm on");

                        // put in an extra int into my_intent

                        // tells the clock that you want a certain value from the drop-down menu/spinner

                        my_intent.putExtra("ringtone_choice", choose_sound);

                        Log.e("The Ringtone id is" , String.valueOf(choose_sound));



                        // create a pending intent that delays the intent

                        // until the specified calendar time

                        pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0,

                                my_intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        Log.e("Alarm Time: ",String.valueOf(new Date(calendar.getTimeInMillis()).toString()));

                        // set the alarm manager
                        //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pending_intent);

                        alarm_manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pending_intent);



                    }
                }

                else if(!alarm_on.isChecked()){
                    //if the user cancels alarm before schedule time
                    Log.e("Alarm Status:","ForceFul Cancel");
                    stopAlarm();
                }
            }
        });






    }

    private void set_alarm_text(String output) {

       // update_text.setText(output);
        alarm_set.setText(output);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choose_sound = (int) l;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        alarm_manager.cancel(pending_intent);

    }


    void showDialog(){
        alBuilder=new AlertDialog.Builder(MainActivity.this);
        String msg="Congratulations, You have Completed Task";
        alBuilder.setMessage(msg);
        alBuilder.setTitle("Alarm Status");
        alBuilder.setCancelable(true);

        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish the Activity

                finish();
            }
        });
        alBuilder.create().show();
    }

    void showInstructions(){
        alBuilder=new AlertDialog.Builder(MainActivity.this);
        String msg="1. Please set the Alarm to suitable time "+"\n"
                +"2. Press the Alarm on Button "+"\n"+
                "3. You will have to shake your phone 10-15 times, in order to Switch off the alarm"+"\n" +
                "4. Don't Enjoy your sleep..."+"\n"+
                "Caution: This app uses the System services and you have no option to shut down the alarm other than"+
                "Doing the Physical Activity, Please choose wisely.";
        alBuilder.setMessage(msg);
        alBuilder.setTitle("Instructions");
        alBuilder.setCancelable(true);

        alBuilder.setPositiveButton("Ready?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish the Activity
               alBuilder.show().dismiss();
            }
        });

        alBuilder.setNegativeButton("I Love Sleep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alBuilder.create().show();
    }


    void stopAlarm(){


                // cancel the alarm

                alarm_manager.cancel(pending_intent);



                // put extra string into my_intent

                // tells the clock that you pressed the "alarm off" button

                my_intent.putExtra("extra", "alarm off");

                // also put an extra int into the alarm off section
                // to prevent crashes in a Null Pointer Exception

                my_intent.putExtra("ringtone_choice", choose_sound);

                notificationOn=true;
                my_intent.putExtra("notification",notificationOn);
                Log.e("Stop Alarm","Service Initiated");
                sendBroadcast(my_intent);



    }

    void setReceiver(){

        myBroadCastReceiver=new MyBroadCastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(FILTER_ACTION_KEY);

        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadCastReceiver,intentFilter);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();


             if(action.equalsIgnoreCase(FILTER_ACTION_KEY)){
                //receive broadcast and then triger the intent to Stop the Alarm
                String s=intent.getStringExtra("Message");
                Log.e("Broadcast Message:",s);
                stopAlarm();
                showDialog();

                //we will kill the  service here
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationOn=true;

    }


}
