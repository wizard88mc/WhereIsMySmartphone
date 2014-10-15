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
        public void updateProgressBar(int percentage);
    }

    public AsyncResponse delegate = null;

    private static final String UPLOAD_SERVER_URI = "trainutri.unige.ch";
    private static final String USERNAME = "matteo";
    private static final String PASSWORD = "mcIman47!";

    private long totalSizeToSend = 0;
    private long totalSizeSent = 0;
    private int totalFilesToSend = 0;
    private int totalFilesSent = 0;

    @Override
    protected Integer doInBackground(File... params)
    {
        totalFilesToSend = params.length;

        for (int i = 0; i < params.length; i++)
        {
            totalSizeToSend += params[i].length();
        }

        FTPClient client = new FTPClient();

        try {

            client.connect(UPLOAD_SERVER_URI, 21);
            client.login(USERNAME, PASSWORD);
            client.setType(FTPClient.TYPE_TEXTUAL);
            client.changeDirectory("whereismysmartphone");

            for (int i = 0; i < params.length; i++)
            {
                client.upload(params[i], new MyTransferListener());
            }

            client.disconnect(true);
        }
        catch(Exception exc)
        {
            return -1;
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer finalResult)
    {
        if (finalResult != -1)
        {
            if (totalFilesSent == totalFilesToSend)
            {
                delegate.uploadCompleted(1);
            }
            else
            {
                delegate.uploadCompleted(0);
            }
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
        public void transferred(int i)
        {
            totalSizeSent += i;
            delegate.updateProgressBar((int) (totalSizeSent * 100 / totalSizeToSend));
        }

        @Override
        public void completed()
        {
            totalFilesSent++;
        }

        @Override
        public void aborted()
        {
            Log.d("MyTransferListener", "aborted");
        }

        @Override
        public void failed()
        {
            Log.d("MyTransferListener", "failed");
        }
    }
}
