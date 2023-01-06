package com.RW.BiologicalProductivity.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RW.BiologicalProductivity.services.ServicesAPI;

@RestController
public class SectorController {
    @GetMapping("/getHeatMap")
    public void getHeatMap() {
        ServicesAPI.getPathToHeatMap(26, 1);
        
    }
    
}
