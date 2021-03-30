package com.example.cz2006.Entity;

//To save Lat and Lng variables from LatLng
public class LocationLatLng{
    public Double locLat;
    public Double getLocLat() {return locLat;}
    public void setLocLat(Double LocLat) {this.locLat = LocLat;}

    public Double locLng;
    public Double getLocLng() {return locLng;}
    public void setLocLng(Double LocLng) {this.locLng = LocLng;}

    public String locCaseSize;

    public String getlocCaseSize() {
        return locCaseSize;
    }

    public void setlocCaseSize(String LocCaseSize) {
        this.locCaseSize = LocCaseSize;
    }
}
