package ch.hackzurich.wifitracker.services;

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
}
