package ch.hackzurich.wifitracker.services;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.hackzurich.wifitracker.models.Capture;
import ch.hackzurich.wifitracker.models.Hotspot;
import ch.hackzurich.wifitracker.models.Position;

public class CaptureService {

    private final int LEVEL = 10;

    private WifiManager wifiManager;

    private String SSID;

    public CaptureService(WifiManager wifiManager, String SSID) {
        this.wifiManager = wifiManager;
        this.SSID = SSID;

        wifiManager.startScan();
    }

    public Capture acquire(double x, double y) {

        List<Hotspot> spots = new LinkedList<>();

        for (ScanResult r: wifiManager.getScanResults()) {
            if (r.SSID.equals(this.SSID)) {
                spots.add(new Hotspot(r.BSSID, r.timestamp, wifiManager.calculateSignalLevel(r.level, LEVEL)));
            }
        }

        wifiManager.startScan();

        return new Capture(SSID, new Date().getTime(), new Position(x, y), "technopark_0", spots);
    }
}
