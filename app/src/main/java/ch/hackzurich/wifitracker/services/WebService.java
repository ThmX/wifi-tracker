package ch.hackzurich.wifitracker.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.hackzurich.wifitracker.models.Capture;

public class WebService {

    private URL mURL;

    public WebService(String url) throws MalformedURLException {
        this.mURL = new URL(url);
    }

    public void send(Capture capture) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();

        Gson gson = new Gson();
        String json = gson.toJson(capture);

        Writer writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
    }

    // For debugging purposes
    public static void preview(Capture capture, Context ctx) {
        Gson gson = new Gson();
        String json = gson.toJson(capture);

        // console output
        Log.i("JSON:", json);

//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput("measurements.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
    }
}
