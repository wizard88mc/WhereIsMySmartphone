package it.cs.unipd.listeners;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import it.cs.unipd.whereismysmartphone.MainActivity;

/**
 * Created by Matteo on 06/06/2014.
 */
public class SamplingStoreService extends IntentService {

    private SensorManager mSensorManager;
    public static Sensor mSensorAccelerometer;
    public static Sensor mSensorLinear;
    public static Sensor mSensorRotation;
    public static Sensor mSensorProximity;
    private SensorsListener sensorsListener;

    public SamplingStoreService() {

        super("SamplingStoreService");
    }

    @Override
    public void onCreate() {

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorsListener = new SensorsListener(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MESSAGE", "onStartCommand");
        sensorsListener.dbAdapter.database.beginTransaction();
        mSensorManager.registerListener(sensorsListener, mSensorRotation, 8000);
        mSensorManager.registerListener(sensorsListener, mSensorProximity, 8000);
        mSensorManager.registerListener(sensorsListener, mSensorAccelerometer, 8000);
        mSensorManager.registerListener(sensorsListener, mSensorLinear, 8000);

        try {
            Thread.sleep(3000);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();

            Vibrator v = (Vibrator)this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            Thread.sleep(4000);

            this.stopSelf();

        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopAccelerometer();
    }

    public void stopAccelerometer() {
        mSensorManager.unregisterListener(sensorsListener, mSensorRotation);
        mSensorManager.unregisterListener(sensorsListener, mSensorLinear);
        mSensorManager.unregisterListener(sensorsListener, mSensorAccelerometer);
        mSensorManager.unregisterListener(sensorsListener, mSensorProximity);

        sensorsListener.dbAdapter.database.setTransactionSuccessful();
        sensorsListener.dbAdapter.database.endTransaction();

        sensorsListener.stopRecordData();

        Log.d("MESSAGE", "Service stopped");
        MainActivity.activateButton();
    }
}
