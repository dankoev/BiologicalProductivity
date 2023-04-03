package com.RW.BiologicalProductivity.config;

import com.RW.BiologicalProductivity.services.DB.Repos.RegionRepo;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public RegionService RegionService(RegionRepo regionRepo){
        return new RegionService(regionRepo);
    }
}