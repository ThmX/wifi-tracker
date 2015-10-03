package ch.hackzurich.wifitracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.services.CaptureService;
import ch.hackzurich.wifitracker.services.WebService;

public class RoomMapActivity extends AppCompatActivity {

    private ImageView mRoomMapImageView;
    private CaptureService mCaptureService;
    private Bitmap mRoomMap;
    private TextView mConsole;
    private List<Capture> mCaptureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //capture service
        mCaptureService = new CaptureService(
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE),
                "hackzurich"
        );

        // Capture List
        mCaptureList = new ArrayList<Capture>();


        // ImageView
        mRoomMapImageView = (ImageView) findViewById(R.id.roomMapImageView);

        // Console
        mConsole = (TextView) findViewById(R.id.consoleRoomMap);

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
                        Capture capture = mCaptureService.acquire(xBitmap, yBitmap);
                        mCaptureList.add(capture);
                        mConsole.setText("Measurement: " + capture.getLevels());

                        // DEBUG
                        Log.i("Number of captures", String.valueOf(mCaptureList.size()));
                        Log.i("CaptureLevels:", capture.getLevels());
                        WebService.preview(capture, mConsole.getContext());


                        // draw
                        Canvas canvas = new Canvas(imageContentMutable);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.RED);
                        canvas.drawCircle(xBitmap, yBitmap, 30, paint);
                        paint.setStrokeWidth(12);
                        if (mCaptureList.size() > 1) {
                            canvas.drawLine(mCaptureList.get(mCaptureList.size() - 2).getX(),
                                    mCaptureList.get(mCaptureList.size() - 2).getY(),
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


}
