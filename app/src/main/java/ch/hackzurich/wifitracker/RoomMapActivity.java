package ch.hackzurich.wifitracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.services.CaptureService;

public class RoomMapActivity extends AppCompatActivity {

    private ImageView mRoomMapImageView;
    private CaptureService mCaptureService;
    private Bitmap mRoomMap;
    private TextView mConsole;
    private List<Capture> captureList;

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
        captureList = new ArrayList<Capture>();


        // ImageView
        mRoomMapImageView = (ImageView) findViewById(R.id.roomMapImageView);

        // Console
        mConsole = (TextView) findViewById(R.id.consoleRoomMap);

        // Default Image
        BitmapFactory.Options options = new BitmapFactory.Options();
        /* TODO check image dimensions first to avoid overflow
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.technopark_0, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        */

        mRoomMap = BitmapFactory.decodeResource(getResources(), R.drawable.technopark_0, options);
        mRoomMapImageView.setImageBitmap(mRoomMap);

        // Starting Values of Image
        ImageView v = mRoomMapImageView;
//         mConsole.append("\n x: " + v.getX() + " \n y: " + v.getY() + "\n width: " + v.getWidth() + " \n height: " + v.getHeight());


        // Listener
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.equals(mRoomMapImageView)) {

                    int[] viewLocation = new int[2];
                    v.getLocationOnScreen(viewLocation);    //viewLocation[0] = x, viewLocation[1] = y

                    float xClickScreen = event.getX();  //getX() seems to be already the relative coordinate in respect to the ImageView
                    float yClickScreen = event.getY();

                    float xClickView = xClickScreen; // - viewLocation[0];
                    float yClickView = yClickScreen; // - viewLocation[1];

                    mConsole.setText("\n xScreen: " + xClickScreen + " \n yScreen: " + yClickScreen + "\n xClickView: " + xClickView + " \n yClickView: " + yClickView);

                    // Paint a red dot at the touched point
                    Bitmap imageContent = ((BitmapDrawable) mRoomMapImageView.getDrawable()).getBitmap();
                    Bitmap imageContentMutable = imageContent.copy(Bitmap.Config.ARGB_8888, true);

                    float widthRatio = imageContentMutable.getWidth() / ((float) v.getWidth()) ;
                    float heightRatio = imageContentMutable.getHeight() / ((float) v.getHeight());

                    float xBitmap = widthRatio * xClickView;
                    float yBitmap = heightRatio * yClickView;

                    Canvas canvas = new Canvas(imageContentMutable);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.RED);
                    canvas.drawCircle(xBitmap, yBitmap, 40, paint);
                    mRoomMapImageView.setImageBitmap(imageContentMutable);

                    // acquire and set x and y
                    Capture capture = mCaptureService.acquire(xBitmap, yBitmap);

                    //TODO do something with the coordinates and the measured data

                }
                return false;
            }
        };

        mRoomMapImageView.setOnTouchListener(touchListener);

    }


}
