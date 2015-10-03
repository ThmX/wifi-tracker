package ch.hackzurich.wifitracker.models;

import java.util.List;

public class Capture {

    private String ssid;

    private long timestamp;

    private Position position;

    private String location;

    private List<Hotspot> hotspots;

    public Capture(String ssid, long timestamp, Position position, String location, List<Hotspot> hotspots) {
        this.ssid = ssid;
        this.timestamp = timestamp;
        this.position = position;
        this.location = location;
        this.hotspots = hotspots;
    }

    public String getSsid() {
        return ssid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Position getPosition() {
        return position;
    }

    public String getLocation() {
        return location;
    }

    public List<Hotspot> getHotspots() {
        return hotspots;
    }

    public void toJson() {
    }

    // get levels as string output
    public String getLevels() {
        String levels = "";
        for(Hotspot s: hotspots) {
            levels = levels + String.valueOf(s.getLevel()) + "; ";
        }
        if (levels.length() >= 2) {
            levels = levels.substring(0, levels.length() - 2);
        }

        return levels;
    }

    @Override
    public String toString() {
        return "Capture{" +
                "ssid='" + ssid + '\'' +
                ", timestamp=" + timestamp +
                ", position=" + position +
                ", location='" + location + '\'' +
                ", hotspots=" + hotspots +
                '}';
    }
}
