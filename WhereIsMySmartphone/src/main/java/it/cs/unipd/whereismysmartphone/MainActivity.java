package it.cs.unipd.whereismysmartphone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity
{
    private Controller mController = null;
    private ProgressDialog dialog = null;
    private static final String UPLOAD_SERVER_URI = "";
    private int serverResponseCode = 0;

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

        mController = new Controller(this);
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
        else if (id == R.id.action_send_db) {
            shareDb();
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
        Log.d("MAIN_ACTIVITY", "onBtnClick");

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

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void shareDb() {
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
        String output_name="whereismysmartphoneAcc_"+df.format(new Date())+".csv";
        String output_nameL = "whereismysmartphoneLin_"+df.format(new Date())+".csv";
        String output_nameS = "whereismysmartphoneSettings_"+df.format(new Date())+".csv";
        try {

            Intent i=new Intent(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<Uri>();

            File file=new File(getExternalFilesDir(null), Logger.BASE_FILE_NAME_ACCELEROMETER); // get private db reference
            if (!file.exists() || file.length()==0) throw new Exception("Empty DB");
            File destination = new File(getApplicationContext().getExternalFilesDir(null), output_name);
            this.copyFile(new FileInputStream(file), new FileOutputStream(destination));
            uris.add(Uri.fromFile(destination));

            file = new File(getExternalFilesDir(null), Logger.BASE_FILE_NAME_LINEAR);
            if (!file.exists() || file.length()==0) throw new Exception("Empty DB Linear");
            destination = new File(getExternalFilesDir(null), output_nameL);
            this.copyFile(new FileInputStream(file), new FileOutputStream(destination));
            uris.add(Uri.fromFile(destination));

            file = new File(getExternalFilesDir(null), Logger.BASE_FILE_NAME_SETTINGS_TRUNK);
            if (!file.exists() || file.length() == 0) throw new Exception("Empty Settings");
            destination = new File(getExternalFilesDir(null), output_nameS);
            this.copyFile(new FileInputStream(file),new FileOutputStream(destination));
            //file = this.getFileStreamPath(output_nameS);
            uris.add(Uri.fromFile(destination));

            i.putExtra(Intent.EXTRA_STREAM, uris);
            //i.putExtra(Intent.EXTRA_EMAIL, new String[]{"whereismysmartphone.math.unipd@gmail.com"});
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"wizard88mc@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "New WhereIsMySmartphone Database");

            i.putExtra(Intent.EXTRA_TEXT, "Here is a new Database of data. Thanks to me.");
            i.setType("plain/text");
            startActivity(Intent.createChooser(i, "Share to"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to export db: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MAIN_ACTIVITY", e.getMessage());
        }
    }

    public void clearDb() {
        mController.deleteFiles();
        Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_SHORT).show();
    }

    private void playSoundAndVibrate() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        Vibrator v = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public int uploadFiles()
    {
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        final String[] files = {Logger.BASE_FILE_NAME_ACCELEROMETER, Logger.BASE_FILE_NAME_LINEAR,
                Logger.BASE_FILE_NAME_SETTINGS_TRUNK};

        File sourceFile = new File(getExternalFilesDir(null), files[0]);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Source File does not exists: " + files[0], Toast.LENGTH_LONG);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_SERVER_URI);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", files[0]);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                + files[0] + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +files[0];

                            Toast.makeText(MainActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("EXCEPTION_UPLOAD", "MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run()
                    {
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
}

