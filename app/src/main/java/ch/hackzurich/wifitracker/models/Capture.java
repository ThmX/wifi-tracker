package ch.hackzurich.wifitracker.models;

import java.util.Arrays;
import java.util.List;

public class Capture {

    private String SSID;

    private List<CaptureSpot> spots;

    private float x;
    private float y;

    // constructors
    public Capture(String SSID, List<CaptureSpot> spots) {
        this.SSID = SSID;
        this.spots = spots;
        this.x = 0;
        this.y = 0;
    }

    public Capture(String SSID, List<CaptureSpot> spots, float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }

    public String getSSID() {
        return SSID;
    }

    public List<CaptureSpot> getSpots() {
        return spots;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void toJson() {
    }

    @Override
    public String toString() {
        return "Capture{" +
                "SSID='" + SSID + '\'' +
                ", spots=" + spots +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
