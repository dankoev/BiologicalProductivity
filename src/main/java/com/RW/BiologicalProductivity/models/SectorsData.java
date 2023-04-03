package com.RW.BiologicalProductivity.models;

import lombok.Data;

@Data
public class SectorsData {
    private String name;
    private double[][] cornerCoords;
    
    public SectorsData(String name, double[][] cornerCoords) {
        this.name = name;
        this.cornerCoords = cornerCoords;
    }
}
