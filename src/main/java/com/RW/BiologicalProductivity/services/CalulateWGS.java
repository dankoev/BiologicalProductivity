package com.RW.BiologicalProductivity.services;


import com.RW.BiologicalProductivity.services.enums.TypeMap;
import com.RW.BiologicalProductivity.services.interfaces.MapsManipulation;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
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
        
        String pathToMap = "mapsInfo/H_UTM.tif";
//        MapData mapData = new MapData(pathToMap,8,8);
//        Mat newMat = mapData.getFillSector(44).toHeatMap();
//        HighGui.imshow("dsas",newMat);
//        HighGui.waitKey();
        MapsManipulation mapsManipulation = new MapsManipulationImpl("mapsInfo/H_UTM.tif","mapsInfo/CFT_UTM.tif",
                "mapsInfo/N_UTM.tif","mapsInfo/T10_UTM.tif",8,8);
        Mat newMat = mapsManipulation.getSectorById(28, TypeMap.ZM).toHeatMap();
        HighGui.imshow("dsas",newMat);
//        HighGui.waitKey();
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
    }
}
