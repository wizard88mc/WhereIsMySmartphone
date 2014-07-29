package it.cs.unipd.listeners;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import java.sql.SQLException;

import it.cs.unipd.database.DBAdapter;
import it.cs.unipd.whereismysmartphone.MainActivity;

/**
 * Created by Matteo on 04/06/2014.
 */
public class SensorsListener implements SensorEventListener {

    private Float lastRotationX = null;
    private Float lastRotationY = null;
    private Float lastRotationZ = null;
    private Float lastValueProximity = null;
    private Context context = null;
    private Long timestampStartRecord = null;
    private Boolean firstStepDone = false;

    private MainActivity activity;

    public DBAdapter dbAdapter;

    public SensorsListener(Context context, MainActivity activity) {

        dbAdapter = new DBAdapter(context);
        try {
            dbAdapter.open();
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }

        this.context = context;
        this.activity = activity;
    }

    public void stopRecordData() {

        lastRotationX = null; lastRotationY = null; lastRotationZ = null;
        lastValueProximity = null; firstStepDone = false;
        timestampStartRecord = null;
        //activity.stopRecordData();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        /*if (event != null) {
            if (event.sensor == mSensorAccelerometer) {
                if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                        lastValueProximity != null) {

                    dbAdapter.saveSampleAccelerometer(event.timestamp, event.values[0], event.values[1],
                            event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                            MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                            MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                            MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                            MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination());
                }

                if (timestampStartRecord == null) {
                    timestampStartRecord = event.timestamp;
                }

                if (!firstStepDone && event.timestamp - timestampStartRecord > 1500000000L) {
                    firstStepDone = true;
                    playSoundAndVibrate();
                }

                if (firstStepDone && event.timestamp - timestampStartRecord > 5500000000L) {
                    playSoundAndVibrate();
                    stopRecordData();
                }
            } else if (event.sensor == MainActivity.mSensorLinear) {
                if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                        lastValueProximity != null) {

                    dbAdapter.saveSampleLinear(event.timestamp, event.values[0], event.values[1],
                            event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                            MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                            MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                            MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                            MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination());
                }
            } else if (event.sensor == MainActivity.mSensorRotation) {
                lastRotationX = event.values[0];
                lastRotationY = event.values[1];
                lastRotationZ = event.values[2];
            } else if (event.sensor == MainActivity.mSensorProximity) {
                Log.d("PROXIMITY", "proximity");
                lastValueProximity = event.values[0];
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void playSoundAndVibrate() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Ringtone r = RingtoneManager.getRingtone(MainActivity.context, notification);
        //r.play();

        //Vibrator v = (Vibrator)MainActivity.context.getSystemService(Context.VIBRATOR_SERVICE);
        //v.vibrate(500);
    }
}
