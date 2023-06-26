package com.RW.BiologicalProductivity.controllers.models;

import com.RW.BiologicalProductivity.services.MapService.MapSector;

public class SectorInfoRequest {
    public final static double noDataValue = -99999.0;
    public final static String noDataString = "No Data";
    private String maxValue = noDataString;
    private String minValue = noDataString;
    private String averageValue = noDataString;
    
    public SectorInfoRequest(MapSector sector) {
        if (sector != null){
            maxValue = getValueFromDouble(sector.getMaxSectorValue());
            minValue = getValueFromDouble(sector.getMinSectorValue());
            averageValue = getValueFromDouble(sector.getAverageSectorValue());
        }
    }
    private String getValueFromDouble(double val){
        if (val == noDataValue){
            return noDataString;
        }
        return String.valueOf(val);
        
    }
    
    public String getMaxValue() {
        return maxValue;
    }
    
    public String getMinValue() {
        return minValue;
    }
    
    public String getAverageValue() {
        return averageValue;
    }
}

