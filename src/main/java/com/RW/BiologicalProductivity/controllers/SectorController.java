package com.RW.BiologicalProductivity.controllers;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.RW.BiologicalProductivity.services.MapService.MapAPI;


@Controller
public class SectorController {

    @GetMapping("/getHeatMap")
    public ResponseEntity<byte[]> getHeatMap() throws IOException {
        Instant start = Instant.now();
        
        // byte[] imageBytes = ServicesAPI.getHeatMapAsBytes(26, 8);
        MapAPI.setColSplit(8);
        MapAPI.setRowSplit(8);
        byte[] imageBytes = MapAPI.getHeatMapAsBytes(26, TypeMap.BP,"\\uploads");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
        return new ResponseEntity<>(imageBytes,headers, HttpStatus.OK);
        
    }    
}
