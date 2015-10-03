package ch.hackzurich.wifitracker.models;

public class CaptureSpot {
    private String BSSID;

    private long timestamp;
    private int level;

    public CaptureSpot(String BSSID, long timestamp, int level) {
        this.BSSID = BSSID;
        this.timestamp = timestamp;
        this.level = level;
    }

    public String getBSSID() {
        return BSSID;
    }

    public int getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CaptureSpot{" +
                "BSSID='" + BSSID + '\'' +
                ", timestamp=" + timestamp +
                ", level=" + level +
                '}';
    }
}