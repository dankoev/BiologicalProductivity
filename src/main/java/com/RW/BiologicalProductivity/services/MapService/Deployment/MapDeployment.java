package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.models.RegionInfo;
import com.RW.BiologicalProductivity.models.SectorsData;
import com.RW.BiologicalProductivity.models.SectorsInfo;
import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.MapService.Deployment.enums.MapSplitSize;
import com.RW.BiologicalProductivity.services.MapService.Deployment.enums.SectorsScope;
import com.RW.BiologicalProductivity.services.MapService.MapAPI;
import com.RW.BiologicalProductivity.services.MapService.MapData;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.Setter;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;

public class MapDeployment{
    
    private static final String defaultPathToMaps= "./maps/";
    private static final String defaultPathToSectors= "./sectors/";
    private static final String pathToSectors= defaultPathToSectors;
    private static final String pathToMaps = defaultPathToMaps;
    
    private String regionName;
    
    public MapDeployment(String regionName) {
        this.regionName = regionName;
    }
    
    /** Write new maps*/
    public  boolean writeMaps(){
        //function upload Maps to default path or new if path has been changed (example path "maps/region_1")
        return false;
    }
    private  boolean addNoteToDB(){
        // function adding data to the DB
        return false;
    }
    public static void createMaps(String regionName,){
    
    }
    


    
}
