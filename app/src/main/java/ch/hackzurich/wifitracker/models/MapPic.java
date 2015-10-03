package ch.hackzurich.wifitracker.models;

public class MapPic {
    private String name;
    private String filename;
    private int orientation;

    public MapPic(String name, String filename, int orientation) {
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
        return "MapPic{" +
                "name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", orientation=" + orientation +
                '}';
    }
}
