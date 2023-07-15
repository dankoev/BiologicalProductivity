package com.RW.BiologicalProductivity.services.DB.services;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.repos.RegionRepo;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class RegionService {
    
    private final RegionRepo regionRepo;
    public RegionService(RegionRepo regionRepo) {
        this.regionRepo = regionRepo;
    }
    public Region getInfo(String name) throws NoSuchValueException {
        return regionRepo.findByName(name)
                .orElseThrow(() -> new NoSuchValueException("There is no region with this name"));
    }
    public List<Region> getRegionsInfo(){
        return regionRepo.findAll();
    }
    @Transactional
    public Region getFullInfo(String name) throws NoSuchValueException {
        return regionRepo.findByName(name)
                .orElseThrow(() -> new NoSuchValueException("There is no region with this name"))
                .clone();
    }
    public boolean hasInfo(String name){
        return regionRepo.findByName(name).isPresent();
    }
    
    // ??? Если Set<MapInfo> null
    @Transactional
    public List<MapInfo> getMapsInfo(String regionName) throws NoSuchValueException {
        Region region = this.getInfo(regionName);
        return region.getMapsInfo()
                .stream().toList();
    }
    @Transactional
    public MapInfo getMapInfo(String regionName, TypeMap typeMap) throws NoSuchValueException {
        Region region = this.getInfo(regionName);
        return region.getMapsInfo()
                .stream()
                .filter(mapInfo -> mapInfo.getType() == typeMap)
                .findAny()
                .orElseThrow(()-> new NoSuchValueException("Map '" + typeMap.name() +
                        "' no exists in region with name '" + regionName + "'"));
    }
    @Transactional
    public void deleteMapInfoById(String regionName, Long mapInfoId) throws NoSuchValueException {
        Region region = this.getInfo(regionName);
        MapInfo deletedMapInfo = region.getMapsInfo()
                .stream()
                .filter( mapInfo -> mapInfo.getId().equals(mapInfoId))
                .findAny()
                .orElseThrow(()-> new NoSuchValueException("mapInfoId = " + mapInfoId +
                        " not exist"));
        region.removeMapInfo(deletedMapInfo);
    }
    public Region save(Region region){
        return regionRepo.save(region);
    }
    
}
