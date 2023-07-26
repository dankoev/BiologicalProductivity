package com.RW.BiologicalProductivity.controllers;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/bp-app/admin")
public class AdminController {
    @Autowired
    RegionService regionService;
    @Autowired
    MapInfoService mapInfoService;
    
    @GetMapping(value="")
    public String admin() {
        return "admin";
    }
    
    @PostMapping(value="/checkMapDir")
    public ResponseEntity checkMapDir() {
        System.out.println("START: uploadServiceTest");
        try{
            MapUploadService m = new MapUploadService(regionService);
            m.checkMapsDirectory();
        }catch (IOException | DataBaseException e){
            System.err.println(e.getMessage());
            return ResponseEntity.status(303)
                    .header("Location", "/admin")
                    .body(e.getMessage());
        }
        return ResponseEntity
                .status(303)
                .header("Location", "/")
                .body("");
    }
    
}
