package com.RW.BiologicalProductivity.services.MapService.interfaces;

import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.opencv.core.Mat;

import java.io.IOException;

public interface MapAPI {
    MapSector getSector(int idSectors, TypeMap typeMap) throws IOException;
    
}
