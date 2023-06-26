package com.RW.BiologicalProductivity.config;

import com.RW.BiologicalProductivity.services.DB.repos.MapInfoRepo;
import com.RW.BiologicalProductivity.services.DB.repos.RegionRepo;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.MapApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public RegionService RegionService(RegionRepo regionRepo){
        return new RegionService(regionRepo);
    }
    @Bean
    public MapInfoService MapInfoService(MapInfoRepo mapInfoRepo){
        return new MapInfoService(mapInfoRepo);
    }
    @Bean
    public MapApiImpl MapApiImpl(RegionService regionService){
        return new MapApiImpl(regionService);
    }

}
