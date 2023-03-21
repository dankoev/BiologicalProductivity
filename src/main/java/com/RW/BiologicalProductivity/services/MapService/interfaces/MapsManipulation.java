package com.RW.BiologicalProductivity.services.interfaces;

import com.RW.BiologicalProductivity.services.MapSector;
import com.RW.BiologicalProductivity.services.enums.TypeMap;

import java.io.IOException;

public interface MapsManipulation {
    
    public MapSector getSectorById(int sectorId, TypeMap typeMap) throws IOException;
}
