package ch.hackzurich.wifitracker.services;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.LinkedList;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.models.CaptureSpot;

public class CaptureService {

    private final int LEVEL = 10;

    private WifiManager wifiManager;

    private String SSID;

    public CaptureService(WifiManager wifiManager, String SSID) {
        this.wifiManager = wifiManager;
        this.SSID = SSID;

        wifiManager.startScan();
    }

    public Capture acquire() {

        List<CaptureSpot> spots = new LinkedList<>();

        for (ScanResult r: wifiManager.getScanResults()) {
            if (r.SSID.equals(this.SSID)) {
                spots.add(new CaptureSpot(r.BSSID, r.timestamp, r.level));
            }
        }

        return new Capture(SSID, spots);

    }

    public Capture acquire(float x, float y) {

        Capture cap = acquire();
        cap.setX(x);
        cap.setY(y);

        return cap;

    }
}
