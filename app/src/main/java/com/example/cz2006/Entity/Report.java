package com.example.cz2006.Entity;

import android.graphics.Bitmap;
import android.location.Location;

public class Report {
    private Bitmap bitmap;
    private String details;
    private Location location;

    public Report(Bitmap bitmap, String details, Location location) {
        this.bitmap = bitmap;
        this.details = details;
        this.location = location;
    }

    public Report() {
        this.bitmap = null;
        this.details = "";
        this.location = null;
    }

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) {this.location = location; }
    public double getLatitude() { return location.getLatitude(); }
    public double getLongitude() { return location.getLongitude(); }
}