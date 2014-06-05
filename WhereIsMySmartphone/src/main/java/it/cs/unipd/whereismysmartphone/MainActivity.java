package it.cs.unipd.whereismysmartphone;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import it.cs.unipd.listeners.SensorsListener;
import it.cs.unipd.utils.Settings;


public class MainActivity extends ActionBarActivity {

    public static Settings experimentSettings;
    private SensorsListener sensorListener = null;
    private boolean recordingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAdapterForSpinner((Spinner)findViewById(R.id.sex), R.array.sex);

        setAdapterForSpinner((Spinner)findViewById(R.id.age), R.array.age);

        setAdapterForSpinner((Spinner)findViewById(R.id.height), R.array.height);

        setAdapterForSpinner((Spinner)findViewById(R.id.hand), R.array.hand);

        setAdapterForSpinner((Spinner)findViewById(R.id.shoes), R.array.shoes);

        setAdapterForSpinner((Spinner)findViewById(R.id.action), R.array.actions);

        setAdapterForSpinner((Spinner)findViewById(R.id.from), R.array.origins);

        setAdapterForSpinner((Spinner) findViewById(R.id.destination), R.array.destinations);

        sensorListener = new SensorsListener(this);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnClick(View view) {

        view.setEnabled(false);
        sensorListener.startRecordData();
        try {
            Thread.sleep(3000);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();

            Thread.sleep(4000);
            sensorListener.stopRecordData();
            view.setEnabled(true);
        }
        catch(InterruptedException exc) {
            exc.printStackTrace();
        }

    }
}
