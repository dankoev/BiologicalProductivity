package com.RW.BiologicalProductivity.services;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import com.RW.BiologicalProductivity.services.models.MapInfo;
import com.google.gson.Gson;

class Calculate {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
    }
    public static String pathToMapHInfo ="mapsInfo/mapHInfo.json";
    public static String pathToMapCFTInfo ="mapsInfo/mapCFTInfo.json";
    public static String pathToMapNInfo ="mapsInfo/mapNInfo.json";
    public static String pathToMapT10Info ="mapsInfo/mapT10Info.json";


    public static String pathToHSectors ="src/main/resources/static/data/sectors/H/";
    public static String pathToCFTSectors ="src/main/resources/static/data/sectors/CFT/";
    public static String pathToNSectors ="src/main/resources/static/data/sectors/N/";
    public static String pathToT10Sectors ="src/main/resources/static/data/sectors/T10/";

    
    public static String pathToZmSectors ="src/main/resources/static/data/sectors/Zm/";
    public static String pathToSYSectors ="src/main/resources/static/data/sectors/SY/";
    public static String pathToBetaHSectors ="src/main/resources/static/data/sectors/BetaH/";
    public static String pathToBPSectors ="src/main/resources/static/data/sectors/BP/";

    protected static MapInfo mapHinfo;
    protected static MapInfo mapCFTinfo;
    protected static MapInfo mapNinfo;
    protected static MapInfo mapT10info;

    protected static MapData mapHData;
    protected static MapData mapNData;
    protected static MapData mapCFTData;
    protected static MapData mapT10Data;
    public static void main(String[] args) {
        readMapInfo();
        createMapsData();
        MapsManipulation manipulation = new MapsManipulation(mapHData, mapCFTData, mapNData, mapT10Data);
        calculateAllInitialMap(manipulation, 1);

        calculateAllResultMap(manipulation, 1);
        calculateAllResultMap(manipulation, 2);
        // calculateAllResultMap(manipulation, 3);
        calculateAllResultMap(manipulation, 4);

    }
    private static void writeSectorToPath(Mat sector, int index, String path){
        Imgcodecs.imwrite(path + "sector" + index + ".jpeg", sector);
        // System.out.println("Сектор " + index +" записан по пути:" + path);
    }

    private static void calculateAllResultMap(MapsManipulation manipulation, int typeMap){
        String path = switch (typeMap) {
            case 1 ->
                pathToZmSectors;
            case 2 ->
                pathToBetaHSectors;
            case 3 ->
                pathToSYSectors;
            case 4->
                pathToBPSectors;
            default -> {
                System.out.println("Hеверный тип результирующей карты");
                yield null;
            }      
        };
        for (int i = 0; i < 64; i++) {
            Mat newMat = manipulation.calculate(i,typeMap).toRGB();
            writeSectorToPath(newMat,i,path);
        } 
        System.out.println("Записаны сектора по пути " + path);
    }
    private static void calculateAllInitialMap(MapsManipulation manip, int typeMap){
        String path = switch (typeMap) {
            case 1 ->
                pathToHSectors;
            case 2 ->
                pathToCFTSectors;
            case 3 ->
                pathToNSectors;
            case 4->
                pathToT10Sectors;
            default -> {
                System.out.println("Hеверный тип изначальной карты");
                yield null;
            }      
        };
        for (int i = 0; i < 64; i++) {
            Mat newMat = manip.getSectorById(i,typeMap).toRGB();
            writeSectorToPath(newMat,i,path);
        } 
        System.out.println("Записаны сектора по пути " + path);
    }
    protected static void readMapInfo(){
        mapHinfo = readJSON(pathToMapHInfo);
        mapCFTinfo = readJSON(pathToMapCFTInfo);
        mapNinfo = readJSON(pathToMapNInfo);
        mapT10info = readJSON(pathToMapT10Info);
    }
    protected static void createMapsData(){
        mapHData = new MapData(mapHinfo.pathToMap, mapHinfo.cornerCoords, mapHinfo.maxMapValue, mapHinfo.minMapValue);
        mapNData = new MapData(mapNinfo.pathToMap, mapNinfo.cornerCoords, mapNinfo.maxMapValue, mapNinfo.minMapValue);
        mapCFTData = new MapData(mapCFTinfo.pathToMap, mapCFTinfo.cornerCoords, mapCFTinfo.maxMapValue, mapCFTinfo.minMapValue);
        mapT10Data = new MapData(mapT10info.pathToMap, mapT10info.cornerCoords, mapT10info.maxMapValue, mapT10info.minMapValue);
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
    
}