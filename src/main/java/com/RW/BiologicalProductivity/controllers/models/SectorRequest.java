package com.RW.BiologicalProductivity.controllers.models;

public class SectorRequest {
    private String type;
    private double[][] coords;
    private double[][] bounds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[][] getCoords() {
        return coords;
    }

    public void setCoords(double[][] coords) {
        this.coords = coords;
    }

    public double[][] getBounds() {
        return bounds;
    }

    public void setBounds(double[][] bounds) {
        this.bounds = bounds;
    }
}
