package com.RW.BiologicalProductivity.controllers.models;

import com.RW.BiologicalProductivity.services.MapService.MapSector;

public class SectorStatisticsResponse {
    private final static double noDataValue = -99999.0;
    private final static String noDataString = "No Data";
    private String maxSectorValue = noDataString;
    private String minSectorValue = noDataString;
    private String averageSectorValue = noDataString;
    private double maxMapValue = noDataValue;
    private double minMapValue = noDataValue;
    

    
    public SectorStatisticsResponse(MapSector sector) {
        if (sector != null){
            maxSectorValue = getValueFromDouble(sector.getMaxSectorValue());
            minSectorValue = getValueFromDouble(sector.getMinSectorValue());
            averageSectorValue = getValueFromDouble(sector.getAverageSectorValue());
            double[] maxMinMapValue = sector.getMaxMinMapValue();
            maxMapValue = maxMinMapValue[0];
            minMapValue = maxMinMapValue[1];
        }
    }
    private String getValueFromDouble(double val){
        if (val == noDataValue){
            return noDataString;
        }
        return String.valueOf(val);
        
    }
    
    public String getMaxSectorValue() {
        return maxSectorValue;
    }
    
    public String getMinSectorValue() {
        return minSectorValue;
    }
    
    public String getAverageSectorValue() {
        return averageSectorValue;
    }
    
    public double getMaxMapValue() {
        return maxMapValue;
    }
    
    public double getMinMapValue() {
        return minMapValue;
    }
}

