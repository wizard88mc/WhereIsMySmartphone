package it.cs.unipd.whereismysmartphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import it.cs.unipd.database.DBAdapter;
import it.cs.unipd.database.DBFileWriter;
import it.cs.unipd.listeners.SamplingStoreService;
import it.cs.unipd.listeners.SensorsListener;
import it.cs.unipd.utils.Settings;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    public static Settings experimentSettings;
    public static boolean recording = false;

    private SensorManager mSensorManager;
    public Sensor mSensorAccelerometer;
    public Sensor mSensorLinear;
    public Sensor mSensorRotation;
    public Sensor mSensorProximity;

    private Float lastRotationX = null;
    private Float lastRotationY = null;
    private Float lastRotationZ = null;
    private Float lastValueProximity = null;
    private Long timestampStartRecord = null;
    private Boolean firstStepDone = false;

    //public DBAdapter dbAdapter;

    private DBFileWriter fileWriter;
    int currentTrunkAccelerometer = 0;
    int currentTrunkLinear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getPreferences(0);

        setAdapterForSpinner((Spinner)findViewById(R.id.sex), R.array.sex);
        ((Spinner)findViewById(R.id.sex)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.sex)).indexOf(preferences.getString("SEX", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.age), R.array.age);
        ((Spinner)findViewById(R.id.age)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.age)).indexOf(preferences.getString("AGE", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.height), R.array.height);
        ((Spinner)findViewById(R.id.height)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.height)).indexOf(preferences.getString("HEIGHT", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.hand), R.array.hand);
        ((Spinner)findViewById(R.id.hand)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.hand)).indexOf(preferences.getString("HAND", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.shoes), R.array.shoes);
        ((Spinner)findViewById(R.id.shoes)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.shoes)).indexOf(preferences.getString("SHOES", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.action), R.array.actions);
        ((Spinner)findViewById(R.id.action)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.actions)).indexOf(preferences.getString("ACTION", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.origin), R.array.origins);
        ((Spinner)findViewById(R.id.origin)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.origins)).indexOf(preferences.getString("ORIGIN", "")));

        setAdapterForSpinner((Spinner) findViewById(R.id.destination), R.array.destinations);
        ((Spinner)findViewById(R.id.destination)).setSelection(Arrays.asList(
                getResources().getStringArray(R.array.destinations)).indexOf(preferences.getString("DESTINATION", "")));

        currentTrunkAccelerometer = preferences.getInt("ACCELEROMETER_LAST_TRUNK", 0);
        currentTrunkLinear = preferences.getInt("LINEAR_LAST_TRUNK", 0);

        //backgroundStoreSampler = new Intent(this, SamplingStoreService.class);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        fileWriter = new DBFileWriter(getApplicationContext());
    }


    private void setAdapterForSpinner(Spinner spinner, int arrayResource) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clearDb) {
            clearDb();
            return true;
        }
        else if (id == R.id.action_upload_db) {
            shareDb();
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void onBtnClick(View view) {

        if (!recording) {

            Log.d("MAIN_ACTIVITY", "onBtnClick");
            view.setEnabled(false);
            recording = true;
            currentTrunkAccelerometer++;
            currentTrunkLinear++;

            String sex = ((Spinner) findViewById(R.id.sex)).getSelectedItem().toString();
            String age = ((Spinner) findViewById(R.id.age)).getSelectedItem().toString();
            String height = ((Spinner) findViewById(R.id.height)).getSelectedItem().toString();
            String shoes = ((Spinner) findViewById(R.id.shoes)).getSelectedItem().toString();
            String hand = ((Spinner) findViewById(R.id.hand)).getSelectedItem().toString();
            String action = ((Spinner) findViewById(R.id.action)).getSelectedItem().toString();
            String origin = ((Spinner) findViewById(R.id.origin)).getSelectedItem().toString();
            String destination = ((Spinner) findViewById(R.id.destination)).getSelectedItem().toString();

            MainActivity.experimentSettings = new Settings(sex, age, height, shoes, hand, action,
                    origin, destination);

            /**
             * Storing preferences
             */
            SharedPreferences settings = this.getPreferences(0);
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

            fileWriter.openOutputStream();
            startRecordData();
        }
    }

    private void startRecordData() {

        Log.d("MAIN_ACTIVITY", "Registering listener");
        mSensorManager.registerListener(this, mSensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorRotation, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorLinear, SensorManager.SENSOR_DELAY_GAME);

        /*new Thread() {
            public void run() {
                try {
                    Thread.sleep(5500);
                }
                catch(InterruptedException exc) {}
                MainActivity.this.stopSensors();
            }
        }.start();*/
    }

    public void stopRecordData() {

        lastRotationX = null; lastRotationY = null; lastRotationZ = null;
        lastValueProximity = null; firstStepDone = false;
        timestampStartRecord = null;
    }

    public void stopSensors() {

        this.recording = false;
        Log.d("MAIN_ACTIVITY","Stopping listeners");
        mSensorManager.unregisterListener(this, mSensorRotation);
        mSensorManager.unregisterListener(this, mSensorLinear);
        mSensorManager.unregisterListener(this, mSensorAccelerometer);
        mSensorManager.unregisterListener(this, mSensorProximity);

        stopRecordData();
        fileWriter.closeOutputStream();
        findViewById(R.id.btnStartRecording).setEnabled(true);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void shareDb() {
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        String output_name="whereismysmartphoneAcc_"+df.format(new Date())+".txt";
        String output_nameL = "whereismysmartphoneLin_"+df.format(new Date())+".txt";
        try {
            ArrayList<Uri> uris = new ArrayList<Uri>();

            File file=new File(getApplicationContext().getFilesDir(), fileWriter.getAccelerometerFilename()); // get private db reference
            //dbAdapter.close();
            if (file.exists()==false || file.length()==0) throw new Exception("Empty DB");
            this.copyFile(new FileInputStream(file), this.openFileOutput(output_name, MODE_WORLD_READABLE));
            file=this.getFileStreamPath(output_name);
            uris.add(Uri.fromFile(file));

            file = new File(getApplicationContext().getFilesDir(), fileWriter.getLinearFilename());
            if (file.exists()==false || file.length()==0) throw new Exception("Empty DB Linear");
            this.copyFile(new FileInputStream(file), this.openFileOutput(output_nameL, MODE_WORLD_READABLE));
            file=this.getFileStreamPath(output_nameL);
            uris.add(Uri.fromFile(file));

            Intent i=new Intent(Intent.ACTION_SEND_MULTIPLE);
            i.putExtra(Intent.EXTRA_STREAM, uris);

            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"whereismysmartphone.math.unipd@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "New WhereIsMySmartphone Database");

            ArrayList<String> extra_text = new ArrayList<String>();
            extra_text.add("Here is a new Database of data. Thanks to me. ");

            i.putExtra(Intent.EXTRA_TEXT, extra_text);
            i.setType("message/rfc822");
            startActivity(Intent.createChooser(i, "Share to"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to export db: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MAIN_ACTIVITY", e.getMessage());
        }
    }

    public void clearDb() {
        fileWriter.deleteFiles();
        Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event != null && this.recording) {
            if (event.sensor == mSensorAccelerometer) {
                if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                        lastValueProximity != null) {

                    //dbAdapter.saveSampleAccelerometer(event.timestamp, event.values[0], event.values[1],
                    fileWriter.addAccelerometerValue(event.timestamp, event.values[0], event.values[1],
                            event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                            MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                            MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                            MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                            MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination(),
                            currentTrunkAccelerometer);

                }

                if (timestampStartRecord == null) {
                    timestampStartRecord = event.timestamp;
                } else if (!firstStepDone && event.timestamp - timestampStartRecord > 1000000000L) {
                    firstStepDone = true;
                    playSoundAndVibrate();
                } else if (firstStepDone && event.timestamp - timestampStartRecord > 5000000000L) {
                    playSoundAndVibrate();
                    stopSensors();
                }

            } else if (event.sensor == mSensorLinear) {
                if (lastRotationX != null && lastRotationY != null && lastRotationZ != null &&
                        lastValueProximity != null) {

                    //dbAdapter.saveSampleLinear(event.timestamp, event.values[0], event.values[1],
                    fileWriter.addLinearValue(event.timestamp, event.values[0], event.values[1],
                            event.values[2], lastRotationX, lastRotationY, lastRotationZ, lastValueProximity,
                            MainActivity.experimentSettings.getSex(), MainActivity.experimentSettings.getAge(),
                            MainActivity.experimentSettings.getHeight(), MainActivity.experimentSettings.getShoes(),
                            MainActivity.experimentSettings.getHand(), MainActivity.experimentSettings.getAction(),
                            MainActivity.experimentSettings.getOrigin(), MainActivity.experimentSettings.getDestination(),
                            currentTrunkAccelerometer);
                }
            } else if (event.sensor == mSensorRotation) {
                lastRotationX = event.values[0];
                lastRotationY = event.values[1];
                lastRotationZ = event.values[2];
            } else if (event.sensor == mSensorProximity) {
                Log.d("PROXIMITY", "proximity: " + Float.toString(event.values[0]));
                lastValueProximity = event.values[0];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void playSoundAndVibrate() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        Vibrator v = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
