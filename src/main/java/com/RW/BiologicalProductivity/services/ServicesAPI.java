package com.RW.BiologicalProductivity.services;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class ServicesAPI extends Calculate {

    private static MapSector calculatedSector;
    public static Mat getHeatMap(int idSectors, int typeMap ){
       return getSector(idSectors, typeMap).toHeatMap();
    }
    public static byte [] getHeatMapAsBytes(int idSectors, int typeMap ){
        Mat heatMap = getHeatMap(idSectors, typeMap);
        byte [] newheatMap = new byte[heatMap.channels() * heatMap.cols() * heatMap.rows()];
        heatMap.get(0,0, newheatMap);
        return newheatMap;
    }
    public static byte [] getHeatMapAsBytes(int idSectors, int typeMap, String pathToUploads){
        Mat heatMap = getHeatMap(idSectors, typeMap);
        byte[] imageBytes = null;
        try {
            if (!Files.exists(Paths.get(pathToUploads)))
                Files.createDirectory(Paths.get(pathToUploads));
            String pathToHeatMap = pathToUploads + "\\sector.jpeg";
            Imgcodecs.imwrite(pathToHeatMap, heatMap);
            System.out.println("******File wrote*****");
            imageBytes = Files.readAllBytes(Paths.get(pathToHeatMap));
            System.out.println("******File read*****");
        } catch (Exception e) {
            System.out.println("Error creating directory or reading file!");
            return null;
        }
        return imageBytes;
    }
    public static String getPathToHeatMap(int idSectors, int typeMap ){
        Mat heatMap = getHeatMap(idSectors, typeMap);
        String pathToFolder = "./uploads/heatMaps/";
        String pathToHeatMap = pathToFolder + "sector.jpeg";
        Imgcodecs.imwrite(pathToHeatMap, heatMap);
        System.out.println("******Файл Загружен*****");
        return pathToHeatMap;
    }
   
    
    protected static MapSector getSector(int idSectors, int typeMap ) {
        Instant start = Instant.now();
        
        readMapInfo();
        
        Instant finish = Instant.now();
        createMapsData();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Чтение JSON заняло, мс: " + elapsed);
        
        MapsManipulation manipulation = new MapsManipulation(mapHData, mapCFTData, mapNData, mapT10Data);
        if (typeMap > 4){
            calculatedSector = manipulation.calculate(idSectors, typeMap - 4);
        }
        else{
            calculatedSector = manipulation.getSectorById(idSectors, typeMap);
        }
        return calculatedSector;
    }
    
}
