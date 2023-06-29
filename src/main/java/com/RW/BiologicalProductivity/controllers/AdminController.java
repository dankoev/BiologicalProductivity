package com.RW.BiologicalProductivity.controllers;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
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
    public RedirectView checkMapDir() {
        System.out.println("START: uploadServiceTest");
        try{
            MapUploadService m = new MapUploadService(regionService,mapInfoService);
            m.checkMapsDirectory();
        }catch (IOException | DataBaseException e){
            System.err.println(e.getMessage());
        }
        return new RedirectView("/");
    }
    
}