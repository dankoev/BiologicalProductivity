package com.RW.BiologicalProductivity.services.DB.services;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.repos.MapInfoRepo;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;

public class MapInfoService {
    MapInfoRepo mapInfoRepo;
    
    public MapInfoService(MapInfoRepo mapInfoRepo) {
        this.mapInfoRepo = mapInfoRepo;
    }
    public MapInfo save(MapInfo info){
       return mapInfoRepo.save(info);
    }
    public MapInfo saveNotesAboutMap(TypeMap typeMap, Region region){
        MapInfo mapInfo = new MapInfo(typeMap.name() + ".tif" ,typeMap);
        mapInfo.setRegion(region);
        return this.save(mapInfo);
    }
}
