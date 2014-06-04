package it.cs.unipd.it.cs.unipd.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Matteo on 04/06/2014.
 */
public class SensorsListener implements SensorEventListener {

    private Float lastRotationX = null;
    private Float lastRotationY = null;
    private Float lastRotationZ = null;


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
