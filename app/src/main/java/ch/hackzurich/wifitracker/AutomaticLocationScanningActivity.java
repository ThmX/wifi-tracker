package ch.hackzurich.wifitracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.models.Compass;
import ch.hackzurich.wifitracker.models.Position;
import ch.hackzurich.wifitracker.services.CaptureService;
import ch.hackzurich.wifitracker.services.WebService;

/**
 * Created by Florian on 04.10.2015.
 */
public class AutomaticLocationScanningActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView mRoomMapImageView;
    private CaptureService mCaptureService;
    private WebService mWebService;
    private SensorManager mSensorManager;
    private Compass mCompass;
    private Bitmap mRoomMap;
    private TextView mConsole;
    private List<Capture> mCaptureList;
    private long mLastTouchTime;
    private double mMapAngleOffset;
    private double mRelStepSize;
    private ArrayList<Float> mAngleTrackList;

    private Timer mScanningTimer;
    private BroadcastReceiver mBroadCastReceiver;

    public static final int SCANNING_PERIOD = 10000;
    public static final int SCANNING_OFFSET = 0;
    private static final int STEPSIZE = 60; // in cm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_automatic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //capture service
        mCaptureService = new CaptureService(
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE),
                "hackzurich"
        );

        // web service
		mWebService = new WebService("http://172.27.7.114:9000/capture");

        // Capture List
        mCaptureList = new ArrayList<Capture>();

        // init compass and step sensors
        mSensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        Sensor mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensorManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        Sensor mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mCompass = new Compass(mSensorManager);
        mCompass.start();

        // ImageView
        mRoomMapImageView = (ImageView) findViewById(R.id.roomMapImageView);

        // Console
        mConsole = (TextView) findViewById(R.id.consoleRoomMap);
        // Map Angle Offset
        mMapAngleOffset = 165.0; // deg

        initializeActivity();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            float[] values = new float[3];
            if (mAngleTrackList == null) {
                mAngleTrackList = new ArrayList<Float>();
            }
            mAngleTrackList.add(new Float(mCompass.getAzimuth()));
//            Log.i("Step detector", Arrays.toString(event.values));
        }
//        else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
////            Log.i("Orientation sensor", Arrays.toString(event.values));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO sensor accuracy...?
    }

    private void initializeActivity(){
        // Capture List
        mCaptureList = new ArrayList<Capture>();

        // Empty console text
        mConsole.setText(null);

        // Default Image
        BitmapFactory.Options options = new BitmapFactory.Options();
        mRoomMap = BitmapFactory.decodeResource(getResources(), R.drawable.technopark_0, options);
        mRoomMapImageView.setImageBitmap(mRoomMap);

        // Starting Values of Image
        ImageView v = mRoomMapImageView;

        // Listener
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.equals(mRoomMapImageView)) {

                    // don't do anything if last event has happened shortly before
                    if (mLastTouchTime != 0) {
                        if (event.getEventTime() - mLastTouchTime < 1000) {
                            return true;
                        }
                        mLastTouchTime = event.getEventTime();
                    }

                    // get image-view coordinates
                    float xImageView = event.getX();
                    float yImageView = event.getY();

                    // map the coordinates onto the bitmap-coordinates
                    float[] coordinates = new float[]{xImageView, yImageView};
                    Matrix matrix = new Matrix();

                    mRoomMapImageView.getImageMatrix().invert(matrix);
                    matrix.postTranslate(mRoomMapImageView.getScrollX(), mRoomMapImageView.getScrollY());
                    matrix.mapPoints(coordinates);

                    float xBitmap = coordinates[0];
                    float yBitmap = coordinates[1];

                    // Paint a red dot at the touched point
                    Bitmap imageContent = ((BitmapDrawable) mRoomMapImageView.getDrawable()).getBitmap();
                    Bitmap imageContentMutable = imageContent.copy(Bitmap.Config.ARGB_8888, true);

                    int widthBitmap = imageContentMutable.getWidth();
                    int heightBitmap = imageContentMutable.getHeight();

                    boolean xBitmapValid = xBitmap > 0 && xBitmap < widthBitmap;
                    boolean yBitmapValid = yBitmap > 0 && yBitmap < heightBitmap;

                    // Calculate relative step size : STEPSIZE (cm) / 100 * widthBitmap / relativeBitmapWhiteSpace / realBuildingSize (m);
                    mRelStepSize = STEPSIZE / 100 * widthBitmap / 1.05 / 110;

                    // if valid coordinates
                    if(xBitmapValid && yBitmapValid){
                        // create capture
                        final Capture capture = mCaptureService.acquire(xBitmap / (float) widthBitmap, yBitmap / (float) heightBitmap);
                        mCaptureList.add(capture);
                        mConsole.setText("Measurements:" + capture.getLevels());

                        // DEBUG
                        Log.i("Number of captures", String.valueOf(mCaptureList.size()));
                        Log.i("CaptureLevels:", capture.getLevels());
                        WebService.preview(capture, mConsole.getContext());


                        new AsyncTask<Capture, Void, Void>() {

                            @Override
                            protected Void doInBackground(Capture... params) {

                                try {
                                    mWebService.send(params[0]);
                                } catch (IOException ex) {
                                    Log.e("AsyncTask", "Error", ex);
                                }
                                return null;
                            }
                        }.execute(capture);

                        // draw
                        Canvas canvas = new Canvas(imageContentMutable);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.RED);
                        canvas.drawCircle(xBitmap, yBitmap, 30, paint);
                        paint.setStrokeWidth(12);
                        if (mCaptureList.size() > 1) {
                            canvas.drawLine(
                                    (float) mCaptureList.get(mCaptureList.size() - 2).getPosition().getX() * widthBitmap,
                                    (float) mCaptureList.get(mCaptureList.size() - 2).getPosition().getY() * heightBitmap,
                                    xBitmap, yBitmap, paint);
                        }
                        mRoomMapImageView.setImageBitmap(imageContentMutable);
                    }
                }
                return true;
            }
        };

        mRoomMapImageView.setOnTouchListener(touchListener);
    }
        public void startStopButtonClicked(View view){
            Button button = (Button) view;

            if(mScanningTimer == null){
                startAutomaticScanning();
            }
            else{
                stopAutomaticScanning();
            }
            // start/stop the
        }

        protected void startAutomaticScanning(){
            registerListener();
            startTimerJob();
        }

    protected void stopAutomaticScanning(){
        releaseListener();
        terminateTimerJob();

        mConsole.setText("Measurements:\n" + mCaptureList.get(mCaptureList.size() - 1).getLevels());
        System.out.println("Scan terminated at " + (new java.util.Date()).toString());
    }

        protected void registerListener(){
            mBroadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("Results received at " + (new java.util.Date()).toString());

                    if(context instanceof AutomaticLocationScanningActivity){
                        AutomaticLocationScanningActivity thisActivity = (AutomaticLocationScanningActivity) context;
                        Capture capture = thisActivity.mCaptureService.getScanResults();
                        thisActivity.estimatePosition(capture);
                        thisActivity.mCaptureList.add(capture);
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(mBroadCastReceiver, filter);
        }

    protected void releaseListener(){
        unregisterReceiver(mBroadCastReceiver);
        mBroadCastReceiver = null;
    }

        protected void startTimerJob(){
            mScanningTimer = new Timer("stepTimer", true);
            TimerTask measurementTask = new TimerTask() {
                @Override
                public void run() {
                    mCaptureService.startScan();
                    System.out.println("Scan started at " + (new java.util.Date()).toString());
                }
            };
            mScanningTimer.schedule(measurementTask, SCANNING_OFFSET, SCANNING_PERIOD);
        }

    protected void terminateTimerJob(){
        mScanningTimer.cancel();
        mScanningTimer = null;
    }

        protected void estimatePosition(Capture capture){
            //TODO deduce the x and y coordinates from the timestamp
        }

	private Capture estimatedCapture() {
        // estimate Position
        Position lastPos = mCaptureList.get(mCaptureList.size() - 1).getPosition();
        Position newPosition = lastPos.walk(mAngleTrackList, mRelStepSize, mMapAngleOffset);
        mAngleTrackList.clear(); // reset step count and angles list

        // capture
        return mCaptureService.acquire(newPosition.getX(), newPosition.getY());
    }


}
