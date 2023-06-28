package com.RW.BiologicalProductivity.controllers.models;

public class SectorRequest {
    private String type;
    private double[][] sectorCoords;
    private double[][] areaCoords;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double[][] getSectorCoords() {
        return sectorCoords;
    }
    
    public void setSectorCoords(double[][] sectorCoords) {
        this.sectorCoords = sectorCoords;
    }
    
    public double[][] getAreaCoords() {
        return areaCoords;
    }
    
    public void setAreaCoords(double[][] areaCoords) {
        this.areaCoords = areaCoords;
    }
}
