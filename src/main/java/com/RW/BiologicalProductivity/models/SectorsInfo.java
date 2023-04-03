package com.RW.BiologicalProductivity.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SectorsInfo {
    private final String type;
    private final int scope;
    private final String pathToSectors;
    private List<SectorsData> sectorsData = new ArrayList<>();
    
    public SectorsInfo(String type, int scope, String pathToSectors) {
        this.type = type;
        this.scope = scope;
        this.pathToSectors = pathToSectors;
    }
    public boolean putSectorData(SectorsData data){
        return sectorsData.add(data);
    }
    
}
