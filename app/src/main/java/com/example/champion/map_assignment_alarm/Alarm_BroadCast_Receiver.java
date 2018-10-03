package com.example.champion.map_assignment_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.champion.map_assignment_alarm.MainActivity.BROADCAST_SEND;

public class Alarm_BroadCast_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Action:",intent.getAction());
        String action=intent.getAction();

        if(action.equalsIgnoreCase(MainActivity.BROADCAST_SEND)){
            Log.e("BD ","We are in Sending Receiver");
            String get_your_string = intent.getExtras().getString("extra");
            Log.e("What is the key? ", get_your_string);
            Integer get_your_ringtone_choice = intent.getExtras().getInt("ringtone_choice");
            Log.e("The RINGTONE choice is ", get_your_ringtone_choice.toString());

            Intent service_intent = new Intent(context, RingtonePlayingService.class);
            service_intent.putExtra("extra", get_your_string);
            service_intent.putExtra("ringtone_choice", get_your_ringtone_choice);
            context.startService(service_intent);
        }
    }
}
