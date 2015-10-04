package ch.hackzurich.wifitracker.models;

import java.util.ArrayList;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Position walk(ArrayList<Float> angleTrackList, double stepSize, double offset) {
        double xCurr = this.x;
        double yCurr = this.y;

        for(Float a:angleTrackList) {
            xCurr = stepSize * Math.cos((double) a);
            yCurr = stepSize * Math.sin((double) a);
        }

        return new Position(xCurr, yCurr);
    }
}