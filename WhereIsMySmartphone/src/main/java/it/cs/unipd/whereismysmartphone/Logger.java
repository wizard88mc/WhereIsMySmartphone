package it.cs.unipd.whereismysmartphone;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import it.cs.unipd.utils.Settings;

/**
 * Created by matteo on 01/10/14.
 */
public class Logger implements MediaScannerConnection.OnScanCompletedListener
{
    private static final String LOG_STRING = "LOGGER";
    public static String BASE_FILE_NAME_ACCELEROMETER = "whereIsMySmartphoneLoggerAccelerometer.csv";
    public static String BASE_FILE_NAME_LINEAR = "whereIsMySmartphoneLoggerLinear.csv";
    public static String BASE_FILE_NAME_SETTINGS_TRUNK = "whereIsMySmartphoneLoggerSettings.csv";

    private File fileAccelerometer = null;
    private File fileLinear = null;
    private File fileSettings = null;
    private FileOutputStream outputStreamAccelerometer = null;
    private FileOutputStream outputStreamLinear = null;
    private FileOutputStream outputStreamSettings = null;

    private static Settings settings = null;

    private Context context;

    public Logger(Context context)
    {
        this.context = context;
        fileAccelerometer = new File(context.getFilesDir(), BASE_FILE_NAME_ACCELEROMETER);
        fileLinear = new File(context.getFilesDir(), BASE_FILE_NAME_LINEAR);
        fileSettings = new File(context.getFilesDir(), BASE_FILE_NAME_SETTINGS_TRUNK);
        try {
            if (!fileAccelerometer.exists())
            {
                fileAccelerometer.createNewFile();
            }
            outputStreamAccelerometer = new FileOutputStream(fileAccelerometer, true);
        }
        catch(Exception exc) {
            Log.d(LOG_STRING, "Unable to create file " + BASE_FILE_NAME_ACCELEROMETER);
            exc.printStackTrace();
        }

        try {
            if (!fileLinear.exists())
            {
                fileLinear.createNewFile();
            }
            outputStreamLinear = new FileOutputStream(fileLinear, true);
        }
        catch(Exception exc) {
            Log.d(LOG_STRING, "Unable to create file " + BASE_FILE_NAME_LINEAR);
            exc.printStackTrace();
        }

        try {
            if (!fileSettings.exists())
            {
                fileSettings.createNewFile();
            }
            outputStreamSettings = new FileOutputStream(fileSettings, true);
        }
        catch(Exception exc) {
            Log.d(LOG_STRING, "Unable to create file " + BASE_FILE_NAME_SETTINGS_TRUNK);
            exc.printStackTrace();
        }
    }

    public static void setExerciseSettings(Settings settings)
    {
        Logger.settings = settings;
    }

    public void writeSettingsForTrunk(int trunkAccelerometer, int trunkLinear)
    {
        String stringSettings = "(" + trunkAccelerometer + "," + trunkLinear + "," +
                settings.toString() + ")\n";
        try {
            outputStreamSettings.write(stringSettings.getBytes());
            outputStreamSettings.close();
        }
        catch(Exception exc)
        {
            Log.d("LOGGER_SETTINGS", exc.toString());
            exc.printStackTrace();
        }
    }

    public void saveRecordAccelerometer(long timestamp, int proximity, float x, float y, float z,
        float rotationX, float rotationY, float rotationZ, Float gravityX, Float gravityY,
        Float gravityZ, Float gyroscopeX, Float gyroscopeY, Float gyroscopeZ, Float magneticFieldX,
        Float magneticFieldY, Float magneticFieldZ, Float ambientTemperature, Float maxAmbientTemperature,
        Float light, Float maxLight, Float pressure, Float maxPressure, Float relativeHumidity,
        Float maxRelativeHumidity, int trunk)
    {
        try {
            outputStreamAccelerometer.write(("(" + trunk + "," + timestamp + "," + proximity + ","
                    + x + "," + y + "," + z + "," + rotationX + "," + rotationY + "," + rotationZ
                    + "," + gravityX + "," + gravityY + "," + gravityZ + "," + gyroscopeX + ","
                    + gyroscopeY + "," + gyroscopeZ + "," + magneticFieldX + "," + magneticFieldY
                    + "," + magneticFieldZ + "," + ambientTemperature + "," + maxAmbientTemperature
                    + "," + light + "," + maxLight + "," + pressure + "," + maxPressure + ","
                    + relativeHumidity + "," + maxRelativeHumidity + ")\n").getBytes());

            //Log.d("LOGGER", "writing data accelerometer");
        }
        catch(Exception exc)
        {
            Log.e("LOGGER EXCEPTION_ACCELEROMETER", exc.toString());
            exc.printStackTrace();
        }
    }

    public void saveRecordLinear(long timestamp, int proximity, float x, float y, float z,
        float rotationX, float rotationY, float rotationZ, Float gravityX, Float gravityY,
        Float gravityZ, Float gyroscopeX, Float gyroscopeY, Float gyroscopeZ, Float magneticFieldX,
        Float magneticFieldY, Float magneticFieldZ, Float ambientTemperature, Float maxAmbientTemperature,
        Float light, Float maxLight, Float pressure, Float maxPressure, Float relativeHumidity,
        Float maxRelativeHumidity, int trunk)
    {
        try {
            outputStreamLinear.write(("(" + trunk + "," + timestamp + "," + proximity + "," + x + ","
                    + y + "," + z + "," + rotationX + "," + rotationY + "," + rotationZ + "," + gravityX
                    + "," + gravityY + "," + gravityZ + "," + gyroscopeX + "," + gyroscopeY + ","
                    + gyroscopeZ + "," + magneticFieldX + "," + magneticFieldY + "," + magneticFieldZ
                    + "," + ambientTemperature + "," + maxAmbientTemperature + "," + light + ","
                    + maxLight + "," + pressure + "," + maxPressure + "," + relativeHumidity + ","
                    + maxRelativeHumidity + ")\n").getBytes());
        }
        catch(Exception exc)
        {
            Log.e("LOGGER_ERROR_LINEAR", exc.toString());
            exc.printStackTrace();
        }
    }

    /**
     * Called when an exercise is completed to flush data of the two outputstream
     */
    public void flushData()
    {
        try {
            //outputStreamAccelerometer.flush();
            //outputStreamLinear.flush();
            outputStreamAccelerometer.close();
            outputStreamLinear.close();

            String paths[] = {fileAccelerometer.getPath(), fileLinear.getPath(), fileSettings.getPath()};
            MediaScannerConnection.scanFile(context, paths, null, this);
        }
        catch(Exception exc)
        {
            Log.e("LOGGER_ERROR_FLUSH", exc.toString());
            exc.printStackTrace();
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {

        Log.d("LOGGER", "Scan completed");
    }

    public void deleteFiles()
    {
        try {
            outputStreamAccelerometer.close();
            outputStreamLinear.close();
            outputStreamSettings.close();
        }
        catch(IOException exc) {}
        fileAccelerometer.delete();
        fileLinear.delete();
        fileSettings.delete();
    }

    public File getFileAccelerometer()
    {
        return this.fileAccelerometer;
    }

    public File getFileLinear()
    {
        return this.fileLinear;
    }

    public File getFileSettings()
    {
        return this.fileSettings;
    }

}
