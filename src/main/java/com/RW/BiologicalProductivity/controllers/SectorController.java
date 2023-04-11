package com.RW.BiologicalProductivity.controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import com.RW.BiologicalProductivity.models.PolygonInfo;
import com.RW.BiologicalProductivity.models.SectorsInfo;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.RW.BiologicalProductivity.services.MapService.MapApiWithMapData;


@RestController
public class SectorController {

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
//    @PostMapping("/getHeatMapByPath")
//    public ResponseEntity<byte[]> getHeatMapByPath(@RequestBody Map<String, String> requestBody) throws IOException {
//        String path = requestBody.get("path");
//        System.out.println(path);
//        byte [] data =  MapApiWithMapData.getHeatMapByPath(path);
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(data);
//    }
//    @GetMapping(value = "/getJSON")
//    public ResponseEntity<byte[]> getJSON() throws JsonParseException, IOException {
//
//        File json = new File("maps/RegionInfo.json");
//        byte[] jsonBytes = Files.readAllBytes(json.toPath());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return new ResponseEntity<>(jsonBytes,headers,HttpStatus.OK);
//
//    }
}
