package com.RW.BiologicalProductivity.services.DB.services;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.Repos.MapInfoRepo;

public class MapInfoService {
    MapInfoRepo mapInfoRepo;
    
    public MapInfoService(MapInfoRepo mapInfoRepo) {
        this.mapInfoRepo = mapInfoRepo;
    }
    public MapInfo save(MapInfo info){
       return mapInfoRepo.save(info);
    }
}
