package com.RW.BiologicalProductivity.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegionInfo {
    private String name;
    private List<SectorsInfo> sectorsInfo = new ArrayList<>();
    
    public RegionInfo(String name) {
        this.name = name;
    }
    public boolean putSectorsInfo(SectorsInfo info){
        return sectorsInfo.add(info);
    }
}
