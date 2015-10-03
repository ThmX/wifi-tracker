package ch.hackzurich.wifitracker.models;

import java.util.Arrays;
import java.util.List;

public class Capture {

    private String SSID;

    private List<CaptureSpot> spots;

    public Capture(String SSID, List<CaptureSpot> spots) {
        this.SSID = SSID;
        this.spots = spots;
    }

    public String getSSID() {
        return SSID;
    }

    public List<CaptureSpot> getSpots() {
        return spots;
    }

    public void toJson() {
    }

    @Override
    public String toString() {
        return "Capture{" +
                "SSID='" + SSID + '\'' +
                ", spots=" + spots +
                '}';
    }
}
