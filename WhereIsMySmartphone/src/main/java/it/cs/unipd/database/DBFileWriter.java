package it.cs.unipd.database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by matteo on 29/07/14.
 */
public class DBFileWriter {

    private String filenameAccelerometer = "whereAccelerometer.txt";
    private String filenameLinear = "whereLinear.txt";
    private FileOutputStream outputStreamAccelerometer;
    private FileOutputStream outputStreamLinear;
    private Context context;
    private File fileAccelerometer;
    private File fileLinear;
    private String stringAccelerometer = "";
    private String stringLinear = "";

    public DBFileWriter(Context context) {
        fileAccelerometer = new File(context.getFilesDir(), filenameAccelerometer);
        fileLinear = new File(context.getFilesDir(), filenameLinear);

        createNewFiles();

        this.context = context;
    }

    public void openOutputStream() {
        Log.d("FILE_WRITER", "Opening output stream");
        try {
            outputStreamAccelerometer = new FileOutputStream(fileAccelerometer, true);
            outputStreamLinear = new FileOutputStream(fileLinear, true);
            stringAccelerometer = "";
            stringLinear = "";
        }
        catch(IOException exc) {
            Log.e("OPEN_OUTPUT", exc.toString());
        }
    }

    public void addAccelerometerValue(long timestamp, float x, float y, float z, float rotationX,
                                      float rotationY, float rotationZ, float proximity, String sex,
                                      String age, String height, String shoes, String hand, String action,
                                      String origin, String destination, int trunk) {

        String stringoutput = "(" + timestamp + ";" + x + ";" + y + ";" + z + ";" + rotationX + ";" +
                rotationY + ";" + rotationZ + ";" + proximity + ";" + sex + ";" + age + ";" + height + ";" +
                shoes + ";" + hand + ";" + action + ";" + origin + ";" + destination + ";" + trunk + ")";

        stringAccelerometer += stringoutput;
    }

    public void addLinearValue(long timestamp, float x, float y, float z, float rotationX,
                                      float rotationY, float rotationZ, float proximity, String sex,
                                      String age, String height, String shoes, String hand, String action,
                                      String origin, String destination, int trunk) {

        String stringoutput = "(" + timestamp + ";" + x + ";" + y + ";" + z + ";" + rotationX + ";" +
                rotationY + ";" + rotationZ + ";" + proximity + ";" + sex + ";" + age + ";" + height + ";" +
                shoes + ";" + hand + ";" + action + ";" + origin + ";" + destination + ";" + trunk + ")";

        stringLinear += stringoutput;
    }

    public void closeOutputStream() {
        Log.d("FILE_WRITER", "Closing output stream");
        try {

            outputStreamAccelerometer.write(stringAccelerometer.getBytes());
            outputStreamLinear.write(stringLinear.getBytes());
            outputStreamAccelerometer.close();
            outputStreamLinear.close();
        }
        catch(IOException exc) {
            Log.e("CLOSE", exc.toString());
        }
    }

    public String getAccelerometerFilename() {
        //return new File(context.getFilesDir(), filenameAccelerometer);

        return filenameAccelerometer;

    }

    public String getLinearFilename() {
        return filenameLinear;
    }

    public void deleteFiles() {
        context.deleteFile(filenameAccelerometer);
        context.deleteFile(filenameLinear);

        createNewFiles();
    }

    private void createNewFiles() {
        try {
            if (!fileAccelerometer.exists()) {
                Log.d("DBFileWriter", "Creating accelerometer file");
                fileAccelerometer.createNewFile();
            }
            else {
                Log.d("DBFileWriter", "Accelerometer file already exists");
            }
            if (!fileLinear.exists()) {
                Log.d("DBFileWriter", "Creating linear file");
                fileLinear.createNewFile();
            }
            else {
                Log.d("DBFileWriter", "Linear file already exists");
                Log.d("DBFileWriter", Long.toString(fileAccelerometer.length()));
            }
        }
        catch(IOException exc) {
            Log.e("DBFileWriter", "Could not create file");
        }
    }
}
