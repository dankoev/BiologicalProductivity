package com.RW.BiologicalProductivity.services.MapService.interfaces;

import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;

import java.io.IOException;

public interface MapsManipulation {
    
    public MapSector getSectorById(int sectorId, TypeMap typeMap) throws IOException;
}
