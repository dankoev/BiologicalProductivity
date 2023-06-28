package com.RW.BiologicalProductivity.controllers;


import com.RW.BiologicalProductivity.controllers.models.SectorStatisticsResponse;
import com.RW.BiologicalProductivity.controllers.models.SectorRequest;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.MapApiImpl;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;


@RestController
public class SectorController {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load OpenCV library");
    }
    @Autowired
    RegionService regionService;
    @Autowired
    MapInfoService mapInfoService;
    @Autowired
    MapApiImpl mapApi;
    
    
    @GetMapping(value = "/getLastSectorStatistics")
    public ResponseEntity<byte[]> getLastSectorStatistics(){
        MapSector curSector = mapApi.getCurrentSector();
        SectorStatisticsResponse info = new SectorStatisticsResponse(curSector);
        Gson gson = new Gson();
        byte[] data = gson.toJson(info, SectorStatisticsResponse.class).getBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }
    @PostMapping("/getHeatMapOfSector")
    public ResponseEntity<Object> getHeatMapOfSector(@RequestBody SectorRequest sectorRequest) {
        try {
            Instant start = Instant.now();
            double[][] sectorCoords = sectorRequest.getSectorCoords();
            double[][] areaCoords = sectorRequest.getAreaCoords();
            String type = sectorRequest.getType();
    
            if(sectorCoords == null) {
                return ResponseEntity.badRequest()
                        .body("Not exist coordinates");
            }
            if(type == null) {
                return ResponseEntity.badRequest()
                        .body("Not exist map type ");
            }
            
            mapApi.detectRegion(sectorCoords);
            byte[] data;
            if (areaCoords != null) {
                data = mapApi.getSectorAsBytes(sectorCoords, areaCoords, TypeMap.getTypeByName(type));
            } else {
                data = mapApi.getSectorAsBytes(sectorCoords, TypeMap.getTypeByName(type));
            }
    
            Instant finish = Instant.now();
            long elapsed = Duration.between(start, finish).toMillis();
            System.out.println("Общее время, мс: " + elapsed);
    
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(data);
            
        } catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
        
    }
}
