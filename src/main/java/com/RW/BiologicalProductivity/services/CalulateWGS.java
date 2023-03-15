package com.RW.BiologicalProductivity.services;


import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class CalulateWGS {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
        
    }
    public static final  int typeMapCoding = Imgcodecs.IMREAD_LOAD_GDAL;
    public static void main(String[] args) throws IOException {
        Instant start = Instant.now();
        
        String pathToMap = "mapsInfo/wgs/CFT_WGS.tif";
        MapData mapData = new MapData(pathToMap);
    
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
    }
}
