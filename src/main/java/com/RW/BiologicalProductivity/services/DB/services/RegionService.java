package com.RW.BiologicalProductivity.services.DB.services;

import com.RW.BiologicalProductivity.services.DB.Repos.RegionRepo;

public class RegionService {
    private final RegionRepo regionRepo;
    
    public RegionService(RegionRepo regionRepo) {
        this.regionRepo = regionRepo;
    }
    
}
