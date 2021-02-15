package com.ubb.paseosVirtuales.model;

import com.google.ar.core.Anchor;

public class Location {
    public float lat;
    public float lon;
    public float z;

    public android.location.Location location;
    public boolean visible;
    public float distance;
    public boolean posicionado = false;

    public void setLocation(float latitude, float longitude) {
        location = new android.location.Location("modelLocation");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    public void setPosicionado(boolean posicionado) {
        this.posicionado = posicionado;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

}