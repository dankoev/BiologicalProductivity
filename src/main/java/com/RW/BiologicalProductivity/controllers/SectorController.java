package com.RW.BiologicalProductivity.controllers;


import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RW.BiologicalProductivity.services.ServicesAPI;


@RestController
public class SectorController {
    @GetMapping("/getHeatMap")
    public void getHeatMap() {
        Instant start = Instant.now();

        ServicesAPI.getPathToHeatMap(26, 8);

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
        
        
    }
    
}
