package ch.hackzurich.wifitracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.services.CaptureService;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private CaptureService mCaptureService;

    private SensorManager mSensorManager;

    private TextView mConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        Sensor mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mSensorManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mCaptureService = new CaptureService(
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE),
                "hackzurich"
        );

        mConsole = (TextView) findViewById(R.id.console);
    }

    public void onCapture(View view) {
        Capture capture = mCaptureService.acquire();
        mConsole.setText(capture.toString());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            Log.i("Step detector", Arrays.toString(event.values));
        }
        else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            Log.i("Orientation sensor", Arrays.toString(event.values));
        }

        Capture capture = mCaptureService.acquire();
        mConsole.setText(capture.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // We'll see about that later
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if (id == R.id.roomMapActivity){
            Intent intent = new Intent(this,RoomMapActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
