package it.cs.unipd.whereismysmartphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.io.File;

import it.cs.unipd.utils.Settings;

/**
 * Created by matteo on 01/10/14.
 */
public class Controller
{
    private SensorManager mSensorManager = null;
    public Sensor mAccelerometer = null;
    public Sensor mLinearSensor = null;
    public Sensor mRotationVector = null;
    public Sensor mProximitySensor = null;
    public Sensor mGyroscope = null;
    public Sensor mLight = null;
    public Sensor mMagneticField = null;
    public Sensor mPressure = null;
    public Sensor mAmbientTemperature = null;
    public Sensor mGravitySensor = null;
    public Sensor mRelativeHumidity = null;

    public Logger logger = null;

    private SensorListener mSensorListener = null;

    private MainActivity view = null;

    public int currentTrunkAccelerometer = -1;
    public int currentTrunkLinear = -1;

    public Settings settings = null;

    public Controller(MainActivity view)
    {
        this.view = view;

        mSensorManager = (SensorManager) view.getSystemService(Context.SENSOR_SERVICE);
        /**
         * Initializing sensors
         */
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        currentTrunkAccelerometer = view.getPreferences(0).getInt("ACCELEROMETER_LAST_TRUNK", 0);
        currentTrunkLinear = view.getPreferences(0).getInt("LINEAR_LAST_TRUNK", 0);

        logger = new Logger(view);
    }

    /**
     * User has clicked the start button, we have start all the sensors and register the listener
     */
    public void readyToStartExercise(String sex, String age, String height, String shoes, String hand,
                                     String action, String origin, String destination)
    {
        logger = new Logger(view);

        currentTrunkAccelerometer++;
        currentTrunkLinear++;

        savePreferences(sex, age, height, shoes, hand, action, origin, destination);

        logger.writeSettingsForTrunk(currentTrunkAccelerometer, currentTrunkAccelerometer);

        mSensorListener = new SensorListener(this, logger);

        mSensorListener.setMaximumRangesValues(
                mProximitySensor != null ? mProximitySensor.getMaximumRange(): null,
                mLight != null ? mLight.getMaximumRange(): null,
                mAmbientTemperature != null ? mAmbientTemperature.getMaximumRange() : null,
                mRelativeHumidity != null ? mRelativeHumidity.getMaximumRange() : null,
                mPressure != null ? mPressure.getMaximumRange() : null);

        mSensorManager.registerListener(mSensorListener, mRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorListener, mProximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        if (mGyroscope != null)
        {
            mSensorManager.registerListener(mSensorListener, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mLight != null)
        {
            mSensorManager.registerListener(mSensorListener, mLight, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mMagneticField != null)
        {
            mSensorManager.registerListener(mSensorListener, mMagneticField, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mAmbientTemperature != null)
        {
            mSensorManager.registerListener(mSensorListener, mAmbientTemperature, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mGravitySensor != null)
        {
            mSensorManager.registerListener(mSensorListener, mGravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mRelativeHumidity != null)
        {
            mSensorManager.registerListener(mSensorListener, mRelativeHumidity, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mPressure != null)
        {
            mSensorManager.registerListener(mSensorListener, mPressure, SensorManager.SENSOR_DELAY_FASTEST);
        }

        try {
            Thread.sleep(200);
            mSensorManager.registerListener(mSensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(mSensorListener, mLinearSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        catch(InterruptedException exc) {}

    }

    private void savePreferences(String sex, String age, String height, String shoes, String hand,
                                 String action, String origin, String destination)
    {
        settings = new Settings(sex, age, height, shoes, hand, action, origin, destination);
        logger.setExerciseSettings(settings);
        /**
         * Storing preferences
         */
        SharedPreferences settings = view.getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("SEX", sex);
        editor.putString("AGE", age);
        editor.putString("HEIGHT", height);
        editor.putString("SHOES", shoes);
        editor.putString("HAND", hand);
        editor.putString("ACTION", action);
        editor.putString("ORIGIN", origin);
        editor.putString("DESTINATION", destination);
        editor.putInt("ACCELEROMETER_LAST_TRUNK", currentTrunkAccelerometer);
        editor.putInt("LINEAR_LAST_TRUNK", currentTrunkLinear);
        editor.commit();
    }

    public void stopRecording()
    {
        mSensorManager.unregisterListener(mSensorListener, mAccelerometer);
        mSensorManager.unregisterListener(mSensorListener, mLinearSensor);
        mSensorManager.unregisterListener(mSensorListener, mRotationVector);
        mSensorManager.unregisterListener(mSensorListener, mProximitySensor);
        mSensorManager.unregisterListener(mSensorListener, mGyroscope);
        mSensorManager.unregisterListener(mSensorListener, mLight);
        mSensorManager.unregisterListener(mSensorListener, mMagneticField);
        mSensorManager.unregisterListener(mSensorListener, mPressure);
        mSensorManager.unregisterListener(mSensorListener, mAmbientTemperature);
        mSensorManager.unregisterListener(mSensorListener, mGravitySensor);
        mSensorManager.unregisterListener(mSensorListener, mRelativeHumidity);

        logger.flushData();
    }

    public void deleteFiles()
    {
        logger.deleteFiles();
    }
}
