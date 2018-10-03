package com.example.champion.map_assignment_alarm;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/*
This class will provide the filter to our values of Acceleration. It will normalize
the 3-D values i.e. Acceleration in x,y and z directions. We will provide the Threshold value
to the filter and we will qualify the Sensor event as "STEP" if it qualifies our threshold value.
 */
public class StepDetector {

    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    Context context;

    // change this threshold according to your sensitivity preferences
    private static final float STEP_THRESHOLD = 20f;

    private static final int STEP_DELAY_NS = 250000000;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private float oldVelocityEstimate = 0;

    private StepListener listener;



    private final static String TAG = "StepDetector";

    private float   mLimit = 10;

    private float   mLastValues[] = new float[3*2];

    private float   mScale[] = new float[2];

    private float   mYOffset;



    private float   mLastDirections[] = new float[3*2];

    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };

    private float   mLastDiff[] = new float[3*2];

    private int     mLastMatch = -1;

    private ArrayList<StepListener> mStepListeners = new ArrayList<>();

    public void registerListener(StepListener listener) {
        this.listener = listener;
    }

    StepDetector(Context context){
        this.context=context;

        //declaration for Algorithm() Function.
        int h = 480;

        mYOffset = h * 0.5f;

        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));

        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

    }

    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = SensorFilter.sum(velRing);

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
            listener.step(timeNs);

            lastStepTimeNs = timeNs;
        }
        oldVelocityEstimate = velocityEstimate;
    }

    //This is the advance algorithm to filter the Values but it introduces the Delay in UI.

    void Algorithm(SensorEvent event){
        Sensor sensor = event.sensor;

        float vSum = 0;
        int j=1;
        for (int i=0 ; i<3 ; i++) {

            final float v = mYOffset + event.values[i] * mScale[j];

            vSum += v;

        }

        int k = 0;

        float v = vSum / 3;



        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));

        if (direction == - mLastDirections[k]) {

            // Direction changed

            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?

            mLastExtremes[extType][k] = mLastValues[k];

            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);



            if (diff > mLimit) {



                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);

                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);

                boolean isNotContra = (mLastMatch != 1 - extType);



                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {

                    Log.i(TAG, "step");

//                    for (StepListener stepListener : mStepListeners) {
//
//                        stepListener.step(0);
//
//                    }
                    listener.step(0);

                    mLastMatch = extType;

                }

                else {

                    mLastMatch = -1;

                }

            }

            mLastDiff[k] = diff;

        }

        mLastDirections[k] = direction;

        mLastValues[k] = v;
    }
}
