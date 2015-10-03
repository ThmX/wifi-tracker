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

    public WebService(String url) {
        try {
            this.mURL = new URL(url);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public void send(Capture capture) throws IOException {

        Gson gson = new Gson();
        String json = gson.toJson(capture);

        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setChunkedStreamingMode(0);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(json.length()));

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            Log.e("WebService", "Error", ex);
            throw ex;
        } finally {
            if (writer != null) writer.close();
        }

        conn.disconnect();
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
