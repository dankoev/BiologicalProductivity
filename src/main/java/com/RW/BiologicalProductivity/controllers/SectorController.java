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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Duration;
import java.time.Instant;


@RestController
@RequestMapping("/api")
public class SectorController {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load OpenCV library");
    }
    @Autowired
    RegionService regionService;
    @Autowired
    MapInfoService mapInfoService;

    @PostMapping("/getHeatMapOfSector")
    public ResponseEntity<Object> getHeatMapOfSector(@RequestBody SectorRequest sectorRequest) {
        System.out.println(sectorRequest.getType());
        try {
            Instant start = Instant.now();
            double[][] areaBounds = sectorRequest.getBounds();
            double[][] areaCoords = sectorRequest.getCoords();
            String type = sectorRequest.getType();
    
            if( areaBounds == null) {
                return ResponseEntity.badRequest()
                        .body("Bounds of area don't provided");
            }
            if(type == null) {
                return ResponseEntity.badRequest()
                        .body("Map type don't provided");
            }
            MapApiImpl mapApi = new MapApiImpl(regionService);
            mapApi.detectRegion(areaBounds);
            byte[] data;
            if (areaCoords != null) {
                data = mapApi.getSectorAsBytes(areaBounds, areaCoords, TypeMap.getTypeByName(type));
            } else {
                data = mapApi.getSectorAsBytes(areaBounds, TypeMap.getTypeByName(type));
            }
    
            Instant finish = Instant.now();
            long elapsed = Duration.between(start, finish).toMillis();
            System.out.println("Общее время, мс: " + elapsed);

            HttpHeaders headers = getHttpHeaders(mapApi);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .headers(headers)
                    .body(data);
            
        } catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
        
    }

    private static HttpHeaders getHttpHeaders(MapApiImpl mapApi) {
        MapSector curSector = mapApi.getCurrentSector();
        SectorStatisticsResponse info = new SectorStatisticsResponse(curSector);
        HttpHeaders headers = new HttpHeaders();
        headers.set("info_heatmap_max", info.getMaxSectorValue());
        headers.set("info_heatmap_min", info.getMinSectorValue());
        headers.set("info_heatmap_avg", info.getAverageSectorValue());
        return headers;
    }
}
