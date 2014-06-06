package it.cs.unipd.listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

    public DBAdapter dbAdapter;

    public SensorsListener(Context context) {

        dbAdapter = new DBAdapter(context);
        try {
            dbAdapter.open();
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void stopRecordData() {

        lastRotationX = null; lastRotationY = null; lastRotationZ = null;
        lastValueProximity = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == SamplingStoreService.mSensorAccelerometer) {
            if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                    lastValueProximity != null) {

                dbAdapter.saveSampleAccelerometer(event.timestamp, event.values[0], event.values[1],
                        event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                        MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                        MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                        MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                        MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination());
            }
        }
        else if (event.sensor == SamplingStoreService.mSensorLinear) {
            if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                    lastValueProximity != null) {

                dbAdapter.saveSampleLinear(event.timestamp, event.values[0], event.values[1],
                        event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                        MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                        MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                        MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                        MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination());
            }
        }
        else if (event.sensor == SamplingStoreService.mSensorRotation) {
            lastRotationX = event.values[0]; lastRotationY = event.values[1]; lastRotationZ = event.values[2];
        }
        else if (event.sensor == SamplingStoreService.mSensorProximity) {
            lastValueProximity = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
