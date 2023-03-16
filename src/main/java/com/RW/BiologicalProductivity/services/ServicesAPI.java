package com.RW.BiologicalProductivity.services;

import com.RW.BiologicalProductivity.services.enums.TypeMap;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class ServicesAPI extends Calculate {
    static {
        readMapInfo();
    }
    private static MapSector calculatedSector;
    private static int  rowSplit = 8;
    private static int  colSplit = 8;
    public static Mat getHeatMap(int idSectors, TypeMap typeMap ) throws IOException{
       return getSector(idSectors, typeMap).toHeatMap();
    }
    public static byte [] getHeatMapAsBytes(int idSectors, TypeMap typeMap) throws IOException{
        Mat heatMap = getHeatMap(idSectors, typeMap);
        byte [] newheatMap = new byte[heatMap.channels() * heatMap.cols() * heatMap.rows()];
        heatMap.get(0,0, newheatMap);
        return newheatMap;
    }
    public static byte [] getHeatMapAsBytes(int idSectors, TypeMap typeMap, String pathToUploads) throws IOException{
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
    public static String getPathToHeatMap(int idSectors, TypeMap typeMap ) throws IOException{
        Mat heatMap = getHeatMap(idSectors, typeMap);
        String pathToFolder = "./uploads/heatMaps/";
        String pathToHeatMap = pathToFolder + "sector.jpeg";
        Imgcodecs.imwrite(pathToHeatMap, heatMap);
        System.out.println("******Файл Загружен*****");
        return pathToHeatMap;
    }
   
    
    protected static MapSector getSector(int idSectors, TypeMap typeMap) throws IOException {
        MapsManipulationImpl manipulation = new MapsManipulationImpl(mapHinfo.pathToMap,
                mapCFTinfo.pathToMap,
                mapNinfo.pathToMap, mapT10info.pathToMap,rowSplit,colSplit);
        return  manipulation.getSectorById(idSectors,typeMap);
    }
    public static void setRowSplit(int rowSplit) {
        ServicesAPI.rowSplit = rowSplit;
    }
    public static void setColSplit(int colSplit) {
        ServicesAPI.colSplit = colSplit;
    }
}
