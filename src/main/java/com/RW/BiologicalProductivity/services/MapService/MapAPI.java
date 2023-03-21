package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.models.MapInfo;
import com.google.gson.Gson;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServicesAPI {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
        readMapInfo();
    }
    public static String pathToMapHInfo ="mapsInfo/wgs/mapHInfo.json";
    public static String pathToMapCFTInfo ="mapsInfo/wgs/mapCFTInfo.json";
    public static String pathToMapNInfo ="mapsInfo/wgs/mapNInfo.json";
    public static String pathToMapT10Info ="mapsInfo/wgs/mapT10Info.json";
    
    
    protected static MapInfo mapHinfo;
    protected static MapInfo mapCFTinfo;
    protected static MapInfo mapNinfo;
    protected static MapInfo mapT10info;
    
    protected static void readMapInfo(){
        mapHinfo = readJSON(pathToMapHInfo);
        mapCFTinfo = readJSON(pathToMapCFTInfo);
        mapNinfo = readJSON(pathToMapNInfo);
        mapT10info = readJSON(pathToMapT10Info);
    }
    protected static MapInfo readJSON(String path){
        try {
            FileReader jsonString = new FileReader(path);
            Gson gson = new Gson();
            MapInfo mapInfo = gson.fromJson(jsonString, MapInfo.class);
            System.out.println("JSON прочитан для карты: "+ mapInfo.name);
            return mapInfo;
        } catch (FileNotFoundException e) {
            System.out.println("Проверте наличие файла"+ path);
            return null;
        }
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
    
    protected static MapSector getSector(int idSectors, TypeMap typeMap) throws IOException {
        MapsManipulationImpl manipulation = new MapsManipulationImpl(mapHinfo, mapCFTinfo,mapNinfo, mapT10info,rowSplit,colSplit);
        return  manipulation.getSectorById(idSectors,typeMap);
    }
    public static void setRowSplit(int rowSplit) {
        ServicesAPI.rowSplit = rowSplit;
    }
    public static void setColSplit(int colSplit) {
        ServicesAPI.colSplit = colSplit;
    }
}
