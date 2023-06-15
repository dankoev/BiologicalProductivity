package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;

import java.io.IOException;

public class MapApiDeployment implements MapAPI {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
    }

    private final MapsManipulation manipulation;
    
    public MapApiDeployment(MapsManipulation manipulation) {
        this.manipulation = manipulation;
    }
    public  MapSector getSector(int idSectors, TypeMap typeMap) throws IOException {
        return  manipulation.getSectorById(idSectors,typeMap);
    }
}
