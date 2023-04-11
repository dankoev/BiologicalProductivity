package com.RW.BiologicalProductivity.services.DB.services;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import com.RW.BiologicalProductivity.services.DB.Repos.RegionRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {
    
    private final RegionRepo regionRepo;
    public RegionService(RegionRepo regionRepo) {
        this.regionRepo = regionRepo;
    }
    public Region getInfo(String name){
        return regionRepo.findByName(name)
                .orElseThrow();
    }
    public boolean hasInfo(String name){
        return regionRepo.findByName(name).isPresent();
    }
    
    // ??? Если Set<MapInfo> null
    @Transactional
    public List<MapInfo> getMapsInfo(String regionName){
        Region region = this.getInfo(regionName);
        return region
                .getMapsInfo()
                .stream().toList();
    }
    public Region save(Region region){
        return regionRepo.save(region);
    }
    
}
