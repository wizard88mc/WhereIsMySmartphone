package it.cs.unipd.whereismysmartphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * This class is responsible to implement onSensorChanged method respond to this event in the
 * appropriate way
 * Created by matteo on 01/10/14.
 */
public class SensorListener implements SensorEventListener
{
    private Controller mController = null;
    private Logger logger = null;

    private int currentValueProximity = 1; // 1=phone outside, 1=phone inside
    private float[] currentValuesGravity = null;
    private float[] currentValuesGyroscope = null;
    private float[] currentValuesRotationVector = null;
    private float[] currentValuesMagneticField = null;
    private Float currentValueAmbientTemperature = null;
    private Float currentValueLight = null;
    private Float currentValuePressure = null;
    private Float currentValueRelativeHumidity = null;

    private Float maxRangeValueProximity = null;
    private Float maxRangeValueLight = null;
    private Float maxRangeAmbientTemperature = null;
    private Float maxRangeRelativeHumidity = null;
    private Float maxRangePressure = null;

    public SensorListener(Controller controller, Logger logger) {
        this.mController = controller; this.logger = logger;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor == mController.mAccelerometer)
        {
            final float[] values = event.values; final long timestamp = event.timestamp;

            new Thread() {

                @Override
                public void run() {
                    // here the logger has to save the data
                    if (currentValuesRotationVector != null) {
                        logger.saveRecordAccelerometer(timestamp, currentValueProximity, values[0],
                                values[1], values[2], currentValuesRotationVector[0], currentValuesRotationVector[1],
                                currentValuesRotationVector[2],
                                currentValuesGravity != null ? currentValuesGravity[0] : null,
                                currentValuesGravity != null ? currentValuesGravity[1] : null,
                                currentValuesGravity != null ? currentValuesGravity[2] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[0] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[1] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[2] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[0] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[1] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[2] : null,
                                currentValueAmbientTemperature, maxRangeAmbientTemperature, currentValueLight,
                                maxRangeValueLight, currentValuePressure, maxRangePressure, currentValueRelativeHumidity,
                                maxRangeRelativeHumidity, mController.currentTrunkAccelerometer);
                    }
                }
            }.start();
        }
        else if (event.sensor == mController.mLinearSensor)
        {
            final float[] values = event.values; final long timestamp = event.timestamp;

            new Thread() {

                @Override
                public void run() {
                    // logger has to write data for the linear sensor
                    if (currentValuesRotationVector != null) {
                        logger.saveRecordLinear(timestamp, currentValueProximity, values[0],
                                values[1], values[2], currentValuesRotationVector[0], currentValuesRotationVector[1],
                                currentValuesRotationVector[2],
                                currentValuesGravity != null ? currentValuesGravity[0] : null,
                                currentValuesGravity != null ? currentValuesGravity[1] : null,
                                currentValuesGravity != null ? currentValuesGravity[2] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[0] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[1] : null,
                                currentValuesGyroscope != null ? currentValuesGyroscope[2] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[0] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[1] : null,
                                currentValuesMagneticField != null ? currentValuesMagneticField[2] : null,
                                currentValueAmbientTemperature, maxRangeAmbientTemperature, currentValueLight,
                                maxRangeValueLight, currentValuePressure, maxRangePressure, currentValueRelativeHumidity,
                                maxRangeRelativeHumidity, mController.currentTrunkLinear);
                    }
                }
            }.start();
        }
        else if (event.sensor == mController.mProximitySensor)
        {
            if (event.values[0] == maxRangeValueProximity)
            {
                currentValueProximity = 1;
            }
            else
            {
                currentValueProximity = 0;
            }
        }
        else if (event.sensor == mController.mRotationVector)
        {
            currentValuesRotationVector = event.values.clone();
        }
        else if (event.sensor == mController.mGravitySensor)
        {
            currentValuesGravity = event.values.clone();
        }
        else if (event.sensor == mController.mGyroscope)
        {
            currentValuesGyroscope = event.values.clone();
        }
        else if (event.sensor == mController.mMagneticField)
        {
            currentValuesMagneticField = event.values.clone();
        }
        else if (event.sensor == mController.mAmbientTemperature)
        {
            currentValueAmbientTemperature = event.values[0];
        }
        else if (event.sensor == mController.mLight)
        {
            currentValueLight = event.values[0];
        }
        else if (event.sensor == mController.mPressure)
        {
            currentValuePressure = event.values[0];
        }
        else if (event.sensor == mController.mRelativeHumidity)
        {
            currentValueRelativeHumidity = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setMaximumRangesValues(Float maxRangeValueProximity, Float maxRangeValueLight,
        Float maxRangeAmbientTemperature, Float maxRangeRelativeHumidity, Float maxRangePressure)
    {
        this.maxRangeValueProximity = maxRangeValueProximity;
        this.maxRangeValueLight = maxRangeValueLight;
        this.maxRangeAmbientTemperature = maxRangeAmbientTemperature;
        this.maxRangeRelativeHumidity = maxRangeRelativeHumidity;
        this.maxRangePressure = maxRangePressure;
    }
}
