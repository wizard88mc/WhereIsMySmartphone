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
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorLinear;
    private Sensor mSensorRotation;
    private Sensor mSensorProximity;

    private DBAdapter dbAdapter;

    public SensorsListener(Context context) {

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        dbAdapter = new DBAdapter(context);
    }

    public void startRecordData() {
        try {
            dbAdapter.open();
            mSensorManager.registerListener(this, mSensorRotation, 8000);
            mSensorManager.registerListener(this, mSensorAccelerometer, 8000);
            mSensorManager.registerListener(this, mSensorLinear, 8000);
            mSensorManager.registerListener(this, mSensorProximity, 8000);
        }
        catch(SQLException exc) {
            exc.printStackTrace();
        }

    }

    public void stopRecordData() {
        mSensorManager.unregisterListener(this, mSensorRotation);
        mSensorManager.unregisterListener(this, mSensorLinear);
        mSensorManager.unregisterListener(this, mSensorRotation);
        mSensorManager.unregisterListener(this, mSensorProximity);
        lastRotationX = null; lastRotationY = null; lastRotationZ = null;
        lastValueProximity = null;
        dbAdapter.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

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
        }
        else if (event.sensor == mSensorLinear) {
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
        else if (event.sensor == mSensorRotation) {
            lastRotationX = event.values[0]; lastRotationY = event.values[1]; lastRotationZ = event.values[2];
        }
        else if (event.sensor == mSensorProximity) {
            lastValueProximity = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
