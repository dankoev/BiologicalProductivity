package com.RW.BiologicalProductivity.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import com.RW.BiologicalProductivity.services.enums.TypeMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.RW.BiologicalProductivity.services.ServicesAPI;


@Controller
public class SectorController {

    @GetMapping("/getHeatMap")
    public ResponseEntity<byte[]> getHeatMap() throws IOException {
        Instant start = Instant.now();
        
        // byte[] imageBytes = ServicesAPI.getHeatMapAsBytes(26, 8);
        ServicesAPI.setColSplit(100);
        ServicesAPI.setRowSplit(100);
        byte[] imageBytes = ServicesAPI.getHeatMapAsBytes(26, TypeMap.BP,"\\uploads");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
        return new ResponseEntity<>(imageBytes,headers, HttpStatus.OK);
        
    }    
}
