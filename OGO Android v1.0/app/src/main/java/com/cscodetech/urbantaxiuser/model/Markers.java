package com.customerogo.app.model;

import com.google.android.gms.maps.model.Marker;

public class Markers {
    public Marker marker;
    public NearCar nearCar;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public NearCar getNearCar() {
        return nearCar;
    }

    public void setNearCar(NearCar nearCar) {
        this.nearCar = nearCar;
    }
}
