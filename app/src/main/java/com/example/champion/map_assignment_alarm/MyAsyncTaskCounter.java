package com.example.champion.map_assignment_alarm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTaskCounter extends AsyncTask<Void,Integer,Boolean> {

    interface AsyncTaskResponse{
        void reponseReceived(Boolean b);
    }

    Context context;
    AsyncTaskResponse asyncTaskResponse;
    MyAsyncTaskCounter(Context c,AsyncTaskResponse asyncTaskResponse){
        this.context=c;
        this.asyncTaskResponse=asyncTaskResponse;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        StepCounterWork.numSteps=0;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        //do the work of Step Counting here
        try{

            final StepCounterWork stepCounterWork=new StepCounterWork(context);
            stepCounterWork.startStepCounter();

            while(StepCounterWork.StepCountingRunning){
                //keep the work of counting the steps
                //publishProgress(StepCounterWork.numSteps);
            }

            Log.e("Return From Task",String.valueOf("Loop Done"));

        }
        catch (Exception e){
            Log.e("Activity Exception:",e.toString());
        }

        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.e("NO OF STEPS:",String.valueOf(values[0]));
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        asyncTaskResponse.reponseReceived(true);
    }
}
