package ch.hackzurich.wifitracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.services.CaptureService;
import ch.hackzurich.wifitracker.services.WebService;

public class MainActivity extends AppCompatActivity {

    private ImageView mRoomMapImageView;
    private CaptureService mCaptureService;
    private WebService mWebService;
    private Bitmap mRoomMap;
    private TextView mConsole;
    private List<Capture> mCaptureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //capture service
        mCaptureService = new CaptureService(
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE),
                "hackzurich"
        );

        mWebService = new WebService("http://172.27.7.114:9000/capture");

        // Capture List
        mCaptureList = new ArrayList<Capture>();

        // ImageView
        mRoomMapImageView = (ImageView) findViewById(R.id.roomMapImageView);

        // Console
        mConsole = (TextView) findViewById(R.id.consoleRoomMap);

        initializeActivity();
    }

    public void initializeActivity(){
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

    public void clearButtonClicked(View view){
        initializeActivity();
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

        else if (id == R.id.AutomaticLocationScanningActivity){
            Intent intent = new Intent(this,AutomaticLocationScanningActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
