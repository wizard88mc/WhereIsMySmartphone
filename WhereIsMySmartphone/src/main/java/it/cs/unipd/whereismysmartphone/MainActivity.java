package it.cs.unipd.whereismysmartphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.Date;

import it.cs.unipd.database.DBAdapter;
import it.cs.unipd.listeners.SamplingStoreService;
import it.cs.unipd.listeners.SensorsListener;
import it.cs.unipd.utils.Settings;


public class MainActivity extends ActionBarActivity {

    public static Settings experimentSettings;
    private Intent backgroundStoreSampler;
    private static View button;
    public static Context context;
    public static boolean recording = false;

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

        backgroundStoreSampler = new Intent(this, SamplingStoreService.class);

        MainActivity.context = getApplicationContext();

        backgroundStoreSampler = new Intent(this, SamplingStoreService.class);
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
            recording = true;
            button = view;

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
            editor.commit();

            startRecordData();
        }
    }

    private void startRecordData() {
        startService(backgroundStoreSampler);
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
        String output_name="whereismysmartphone_"+df.format(new Date())+".db";
        try {
            DBAdapter dbAdapter = new DBAdapter(this); // get reference to db connection
            dbAdapter.open();
            File file=new File(dbAdapter.getDBPath()); // get private db reference
            dbAdapter.close();
            if (file.exists()==false || file.length()==0) throw new Exception("Empty DB");
            this.copyFile(new FileInputStream(file), this.openFileOutput(output_name, MODE_WORLD_READABLE));
            file=this.getFileStreamPath(output_name);
            Intent i=new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"whereismysmartphone.math.unipd@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "New WhereIsMySmartphone Database");
            i.putExtra(Intent.EXTRA_TEXT, "Here is a new Database of data. Thanks to me. ");
            i.setType("message/rfc822");
            startActivity(Intent.createChooser(i, "Share to"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to export db: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MAIN_ACTIVITY", e.getMessage());
        }
    }

    public void clearDb() {
        DBAdapter dbAdapter = new DBAdapter(this);
        try {
            dbAdapter.open();
            dbAdapter.cleanDB();
            Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_SHORT).show();
            dbAdapter.close();
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }
    }
}
