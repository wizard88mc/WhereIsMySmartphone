package it.cs.unipd.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import it.cs.unipd.whereismysmartphone.Logger;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by matteo on 14/10/14.
 */
public class SenderTask extends AsyncTask<File, Void, Integer>
{

    public interface AsyncResponse {
        public void uploadCompleted(Integer result);
    }

    public AsyncResponse delegate = null;

    private static final String UPLOAD_SERVER_URI = "trainutri.unige.ch";
    private static final String USERNAME = "matteo";
    private static final String PASSWORD = "mcIman47!";

    @Override
    protected Integer doInBackground(File... params)
    {

        int counter = 0;
        for (int i = 0; i < params.length; i++)
        {
            if (!params[i].isFile()) {

                return 0;

            }
            else {

                FTPClient client = new FTPClient();

                try {

                    client.connect(UPLOAD_SERVER_URI, 21);
                    client.login(USERNAME, PASSWORD);
                    client.setType(FTPClient.TYPE_TEXTUAL);
                    client.changeDirectory("whereismysmartphone");

                    client.upload(params[i], new MyTransferListener());
                    counter++;
                }
                catch(Exception exc)
                {
                    exc.printStackTrace();
                    try {
                        client.disconnect(true);
                    }
                    catch(Exception exc1) {
                        exc1.printStackTrace();
                    }
                }
            } // End else block
        }
        return counter;
    }

    @Override
    protected void onPostExecute(Integer updatedFiles)
    {
        if (updatedFiles ==  3)
        {
            delegate.uploadCompleted(1);
        }
        else
        {
            delegate.uploadCompleted(-1);
        }
    }

    public class MyTransferListener implements FTPDataTransferListener {

        @Override
        public void started() {
            Log.d("STARTED", "STARTED");
        }

        @Override
        public void transferred(int i) {
            Log.d("TRANSFERRED", String.valueOf(i));
        }

        @Override
        public void completed() {
            Log.d("COMPLETED", "COMPLETED");
        }

        @Override
        public void aborted() {
            Log.d("MyTransferListener", "aborted");
        }

        @Override
        public void failed() {
            Log.d("MyTransferListener", "failed");
        }
    }
}
