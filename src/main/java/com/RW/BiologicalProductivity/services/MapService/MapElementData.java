package com.RW.BiologicalProductivity.services;

public class MapElementData {
    private final static double noDataValue = -99999.0;

    public int x;
    public int y;
    public double[] wordCoordinate = {0,0};
    public double value = noDataValue;
    public boolean isNoData = true;

    public MapElementData() {

    }
    
    public void setData(double value, int x, int y, double[] wordCoordinate){
        this.value = value;
        this.x = x;
        this.y = y;
        this.isNoData = value == noDataValue;
        this.wordCoordinate = wordCoordinate;
    }

    public void setData(double value, int x, int y){
            this.value = value;
            this.x = x;
            this.y = y;
            this.isNoData = value == noDataValue;
    }
}
