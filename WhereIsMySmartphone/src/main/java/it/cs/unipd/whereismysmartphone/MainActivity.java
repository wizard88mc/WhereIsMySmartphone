package it.cs.unipd.whereismysmartphone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.cs.unipd.utils.SenderTask;

public class MainActivity extends ActionBarActivity implements SenderTask.AsyncResponse
{
    private Controller mController = null;
    private ProgressDialog dialog = null;
    private File finalFileAccelerometer = null;
    private File finalFileLinear = null;
    private File finalFileSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.setProperty("ftp4j.activeDataTransfer.acceptTimeout", "0");

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

        mController = new Controller(this);
    }


    private void setAdapterForSpinner(Spinner spinner, int arrayResource) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                arrayResource, android.R.layout.simple_spinner_item);
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
            uploadFiles();
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    public void onBtnClick(final View view)
    {
        final String sex = ((Spinner) findViewById(R.id.sex)).getSelectedItem().toString();
        final String age = ((Spinner) findViewById(R.id.age)).getSelectedItem().toString();
        final String height = ((Spinner) findViewById(R.id.height)).getSelectedItem().toString();
        final String shoes = ((Spinner) findViewById(R.id.shoes)).getSelectedItem().toString();
        final String hand = ((Spinner) findViewById(R.id.hand)).getSelectedItem().toString();
        final String action = ((Spinner) findViewById(R.id.action)).getSelectedItem().toString();
        final String origin = ((Spinner) findViewById(R.id.origin)).getSelectedItem().toString();
        final String destination = ((Spinner) findViewById(R.id.destination)).getSelectedItem().toString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.btnStartStop).setEnabled(false);
                new Thread() {
                    public void run() {
                        mController.readyToStartExercise(sex, age, height, shoes, hand, action,
                                origin, destination);

                        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        executor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                playSoundAndVibrate();
                            }
                        }, 4, TimeUnit.SECONDS);

                        ScheduledExecutorService secondExecutor = Executors.newScheduledThreadPool(1);
                        secondExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mController.stopRecording();
                                        MainActivity.this.findViewById(R.id.btnStartStop).setEnabled(true);
                                        playSoundAndVibrate();
                                    }
                                });

                            }
                        }, 8, TimeUnit.SECONDS);
                    }
                }.start();
            }
        });
    }

    private void copyFile(File src, File dst) throws IOException {

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void clearDb() {
        mController.deleteFiles();
        Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_LONG).show();
    }

    private void playSoundAndVibrate() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        Vibrator v = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public void uploadFiles()
    {

        runOnUiThread(new Runnable() {
            public void run() {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle(R.string.uploading);
                dialog.setMax(100);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.show();
            }
        });

        String IMEI = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        Calendar cal = Calendar.getInstance();
        String calendar = String.valueOf(cal.get(Calendar.YEAR)) + (cal.get(Calendar.MONTH) +1)
                + cal.get(Calendar.DAY_OF_MONTH)
                + cal.get(Calendar.HOUR) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) +
                cal.get(Calendar.MILLISECOND);

        finalFileAccelerometer = new File(getFilesDir(), IMEI
                + "_WhereIsMySmartphoneAccelerometer_" + calendar + ".csv");
        finalFileLinear = new File(getFilesDir(), IMEI + "_WhereIsMySmartphoneLinear_"
                + calendar + ".csv");
        finalFileSettings = new File(getFilesDir(), IMEI + "_WhereIsMySmartphoneSettings_"
                + calendar + ".csv");

        try {
            copyFile(mController.logger.getFileAccelerometer(), finalFileAccelerometer);
            copyFile(mController.logger.getFileLinear(), finalFileLinear);
            copyFile(mController.logger.getFileSettings(), finalFileSettings);

            SenderTask task = new SenderTask();
            task.delegate = this;

            task.execute(finalFileAccelerometer, finalFileLinear, finalFileSettings);
        }
        catch(Exception exc)
        {
            Toast.makeText(this, "Unable to store and save file. Please try with email.",
                    Toast.LENGTH_SHORT).show();
            exc.printStackTrace();
        }
    }

    @Override
    public void uploadCompleted(Integer result)
    {
        finalFileSettings.delete();
        finalFileLinear.delete();
        finalFileSettings.delete();

        dialog.dismiss();

        if (result == 1)
        {
            Toast.makeText(this, "Files upload completed. Thank you.", Toast.LENGTH_LONG).show();
            clearDb();
        }
        else if (result == -1 || result == 0)
        {
            Toast.makeText(this, "Files NOT uploaded. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateProgressBar(int percentage)
    {
        dialog.setProgress(percentage);
    }
}

