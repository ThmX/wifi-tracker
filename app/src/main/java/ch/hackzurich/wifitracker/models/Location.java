package ch.hackzurich.wifitracker.models;

public class Location {
    private String name;
    private String filename;
    private int orientation;

    public Location(String name, String filename, int orientation) {
        this.name = name;
        this.filename = filename;
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public int getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", orientation=" + orientation +
                '}';
    }
}
