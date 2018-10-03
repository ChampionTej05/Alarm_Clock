package com.example.champion.map_assignment_alarm;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class StepCounterWork  implements SensorEventListener,StepListener{


      /*
   This class is the monitor class to access the sensor, apply the filter to the values
   and the values will be displayed on the screen.

   Accuracy will be invariant for the sensor and we will try to improve it later.

   It implements the Interface to SensorEventListener and StepListener which is our custom interface.
    */



        private StepDetector simpleStepDetector;
        private SensorManager sensorManager;
        private Sensor accel;
        private static final String TEXT_NUM_STEPS = "Number of Steps: ";
        public static int numSteps;

        static Boolean StepCountingRunning=false;

        Context context;

        StepCounterWork(Context c){
            this.context=c;
        }


        void startStepCounter(){
            numSteps = 0;

            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            simpleStepDetector = new StepDetector(context);
            simpleStepDetector.registerListener(this);
            if (accel!=null){
                sensorManager.registerListener((SensorEventListener) this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                StepCountingRunning=true;

            }

            else Log.e("Sensor : ","Sensor Not Present");
        }

        void stopStepCounter(){
            sensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if(numSteps>=10){

                StepCountingRunning=false;
                Log.e("Running: ",String.valueOf(StepCountingRunning));
                sensorManager.unregisterListener(this);


            }
            else{
                if(event.sensor.getType()== Sensor.TYPE_ORIENTATION){
                    //don't invoke the sensor if we just change the direction

                }
                else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    //returns the Acceleration Values
                   // simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
         simpleStepDetector.Algorithm(event);
                }
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //do nothing
        }

        @Override
        public void step(long timeNS) {
            numSteps++;
            Log.e("NoOfSteps: ",String.valueOf(numSteps));
        }
}
