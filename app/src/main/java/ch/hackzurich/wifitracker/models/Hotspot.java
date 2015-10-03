package ch.hackzurich.wifitracker.models;

public class Hotspot {
    private String bssid;
    private long timestamp;
    private int level;

    public Hotspot(String bssid, long timestamp, int level) {
        this.bssid = bssid;
        this.timestamp = timestamp;
        this.level = level;
    }

    public String getBssid() {
        return bssid;
    }

    public int getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Hotspot{" +
                "bssid='" + bssid + '\'' +
                ", timestamp=" + timestamp +
                ", level=" + level +
                '}';
    }
}