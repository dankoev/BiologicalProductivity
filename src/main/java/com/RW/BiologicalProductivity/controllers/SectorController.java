package com.RW.BiologicalProductivity.controllers;


import com.RW.BiologicalProductivity.controllers.models.SectorRequest;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.MapApiImpl;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.aspectj.weaver.World;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@RestController
public class SectorController {
    @Autowired
    RegionService regionService;
    @Autowired
    MapInfoService mapInfoService;
    
    
    //    @GetMapping("/getHeatMap")
//    public ResponseEntity<byte[]> getHeatMap() throws IOException {
//        Instant start = Instant.now();
//
//        // byte[] imageBytes = ServicesAPI.getHeatMapAsBytes(26, 8);
//        MapApiWithMapData.setColSplit(8);
//        MapApiWithMapData.setRowSplit(8);
//        byte[] imageBytes = MapApiWithMapData.getHeatMapAsBytes(26, TypeMap.BP);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG);
//
//        Instant finish = Instant.now();
//        long elapsed = Duration.between(start, finish).toMillis();
//        System.out.println("Прошло времени, мс: " + elapsed);
//        return new ResponseEntity<>(imageBytes,headers, HttpStatus.OK);
//
//    }
//    @PostMapping(value = "/getSectorsInfoByPolygon")
//    public ResponseEntity<byte[]> getSectorsInfoByPolygon(@RequestBody PolygonInfo info) throws JsonParseException, IOException {
//        System.out.println(info);
////        SectorsInfo sectorsInfo = MapApiImpl.getSectorsInfoByPolygon(info);
//        SectorsInfo sectorsInfo = null;
//        Gson gson = new Gson();
//        byte[] data = gson.toJson(sectorsInfo,SectorsInfo.class).getBytes();
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(data);
//    }
    @PostMapping("/getHeatSector")
    public ResponseEntity<Object> getHeatMapByPath(@RequestBody SectorRequest sectorRequest) {
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
    
            MapApiImpl api = new MapApiImpl(regionService);
            api.detectRegion(new double[]{0});
            byte[] data;
            if (areaCoords != null) {
                data = api.getSectorAsBytes(sectorCoords, areaCoords, TypeMap.getTypeByName(type));
            } else {
                data = api.getSectorAsBytes(sectorCoords, TypeMap.getTypeByName(type));
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
