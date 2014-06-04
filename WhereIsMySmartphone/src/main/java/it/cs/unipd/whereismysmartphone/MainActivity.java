package it.cs.unipd.whereismysmartphone;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity {

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
}
