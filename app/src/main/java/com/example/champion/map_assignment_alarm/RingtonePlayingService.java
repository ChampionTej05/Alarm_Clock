package com.example.champion.map_assignment_alarm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/*
This is the service class, it is heart of our app which fires the service, which plays the selected
audio using media player object. When we receive the intent to stop the alarm, we will stop the mediaplayer
and use the stopSelf() to stop the service.
 */

public class RingtonePlayingService extends Service implements MyAsyncTaskCounter.AsyncTaskResponse {

    MediaPlayer media_song;

    int startId;

     boolean isRunning;
     static boolean shouldContinue=true;
    NotificationManager notify_manager;


    int NOTIFICATION_ID=55;

    MyAsyncTaskCounter myAsyncTaskCounter;

    Integer ringtone_sound_choice;
    String state;

    Notification notification_popup;

    Intent broadCastIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public int onStartCommand(Intent intent, int flags, int startId){
        broadCastIntent=intent;
        performAction(intent);

        return START_NOT_STICKY;
    }






    @RequiresApi(api = Build.VERSION_CODES.M)
    void performAction(Intent intent){
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // fetch the extra string from the alarm on/alarm off values
        if(intent==null){
            Log.e("Notifiation: ","Status Is here");

            //NotificationManager notificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
            try{
                notify_manager.cancel(NOTIFICATION_ID);
            }
            catch (NullPointerException e){
                Log.e("Notification: ",String.valueOf(e.toString()));
            }


            stopSelf();
        }


        else{

            if(shouldContinue==false){
                Log.e("ISRUNNIG STATUS","Coming here");
                stopSelf();
                return;
            }

             state = intent.getExtras().getString("extra");

            // fetch the ringtone choice integer values

             ringtone_sound_choice = intent.getExtras().getInt("ringtone_choice");



            Log.e("Ringtone extra is ", state);

            Log.e("Ringtone choice is ", ringtone_sound_choice.toString());



            // put the notification here, test it out



            // notification

            // set up the notification service

            notify_manager = (NotificationManager)

                    getSystemService(NOTIFICATION_SERVICE);

            // set up an intent that goes to the Main Activity

            Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);

            // set up a pending intent

            PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,

                    intent_main_activity, 0);



            // make the notification parameters

             notification_popup = new Notification.Builder(this)

                    .setContentTitle("Alarm is Ringing, GET UP!!!")

                    .setContentText("Click me!")
                    .setSmallIcon(R.drawable.lol)
                    .setContentIntent(pending_intent_main_activity)
                    .build();

            // this converts the extra strings from the intent

            // to start IDs, values 0 or 1

            assert state != null;

            switch (state) {

                case "alarm on":

                    startId = 1;

                    break;

                case "alarm off":

                    startId = 0;

                    Log.e("Start ID is ", state);

                    break;

                default:

                    startId = 0;

                    break;

            }

            // if there is music playing, and the user pressed "alarm off"

            // music should stop playing

            if (this.isRunning && startId == 0) {

                Log.e("Alarm State", "Alarm Turned OFF");



                // stop the ringtone

                media_song.stop();

                media_song.release();



                this.isRunning = false;

                this.startId = 0;
                notify_manager.cancel(NOTIFICATION_ID);
                //stop the service


                {
                    stopSelf();
                }



            }



            // if there is no music playing, and the user pressed "alarm off"



            else if (!this.isRunning && startId == 0) {



                this.isRunning = false;

                this.startId = 0;
                if(isNotificationVisible()){
                    notify_manager.cancel(NOTIFICATION_ID);
                }


            }



            // if there is music playing and the user pressed "alarm on"

            // do nothing

            else if (this.isRunning && startId == 1) {


                this.isRunning = true;

                this.startId = 1;

            }



            //music is not playing and user presses "alarm on"
            else if(this.isRunning==false && startId==1){
                if(shouldContinue==false){
                    Log.e("RUNNING STATUS","Coming here Media");
                    stopSelf();
                    return;
                }
                MainActivity.alarm_on.setVisibility(View.GONE);
                startMediaPlayer();
            }


        }
    }


    //checks whether our notification exists in the Status bar or not
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNotificationVisible() {
        StatusBarNotification[] notifications = notify_manager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == NOTIFICATION_ID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        this.isRunning = false;
        Log.e("on Destroy called", "Service Destroyed");

        super.onDestroy();



    }



    @Override
    public void reponseReceived(Boolean b) {
            if(b==true){
                Log.e("Received Response:",String.valueOf(b));

                //send the BroadCast;

                if(broadCastIntent!=null){
                    broadCastIntent.setAction(MainActivity.FILTER_ACTION_KEY);
                    broadCastIntent.putExtra("Message","Work is Done");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadCastIntent);
                }



            }
    }

    void startMediaPlayer(){
        Log.e("Media Player ", "Started The Alarm Ringtone");



        this.isRunning = true;

        this.startId = 0;



        // set up the start command for the notification

        notify_manager.notify(NOTIFICATION_ID, notification_popup);

        if(ringtone_sound_choice==0 || ringtone_sound_choice==1)
        {
            //nothing selected so play a default sound or also if 1st is selected
            //default is ringtone 1
try{
    media_song=MediaPlayer.create(getApplicationContext(),R.raw.ringtone_01);
    media_song.setLooping(true);
    media_song.start();
}

catch (Exception e){
    Log.e("Media Player:",e.toString());
}


        }
        else if(ringtone_sound_choice==2){
            media_song=MediaPlayer.create(this,R.raw.ringtone_02);
            media_song.start();
        }
        else if(ringtone_sound_choice==3){
            media_song=MediaPlayer.create(this,R.raw.ringtone_03);
            media_song.start();
        }
        else if(ringtone_sound_choice==4){
            media_song=MediaPlayer.create(this,R.raw.ringtone_04);
            media_song.start();
        }

        //start the step counter here

        myAsyncTaskCounter=new MyAsyncTaskCounter(getApplicationContext(),this);
        myAsyncTaskCounter.execute();
    }
}

