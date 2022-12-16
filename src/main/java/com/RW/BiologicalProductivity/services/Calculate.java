package com.RW.BiologicalProductivity.services;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

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
    private static MapInfo mapHinfo;
    private static MapInfo mapCFTinfo;
    private static MapInfo mapNinfo;
    private static MapInfo mapT10info;

    private static MapData mapHData;
    private static MapData mapNData;
    private static MapData mapCFTData;
    private static MapData mapT10Data;

    public static void main(String[] args) {
        readMapInfo();
        createMapsData();

        MapsManipulation manipulation = new MapsManipulation(mapHData, mapCFTData, mapNData, mapT10Data);
        MapSector sector = manipulation.calculate(26, 4);

        HighGui.namedWindow("Input", HighGui.WINDOW_AUTOSIZE);

        Mat newMat = sector.sectorToRGB();
        HighGui.imshow("Input", newMat);
        HighGui.waitKey();
        
    }
    private static void readMapInfo(){
        mapHinfo = readJSON(pathToMapHInfo);
        mapCFTinfo = readJSON(pathToMapCFTInfo);
        mapNinfo = readJSON(pathToMapNInfo);
        mapT10info = readJSON(pathToMapT10Info);
    }
    private static void createMapsData(){
        mapHData = new MapData(mapHinfo.pathToMap, mapHinfo.cornerCoords, mapHinfo.maxMapValue, mapHinfo.minMapValue);
        mapNData = new MapData(mapNinfo.pathToMap, mapNinfo.cornerCoords, mapNinfo.maxMapValue, mapNinfo.minMapValue);
        mapCFTData = new MapData(mapCFTinfo.pathToMap, mapCFTinfo.cornerCoords, mapCFTinfo.maxMapValue, mapCFTinfo.minMapValue);
        mapT10Data = new MapData(mapT10info.pathToMap, mapT10info.cornerCoords, mapT10info.maxMapValue, mapT10info.minMapValue);
    }
    public static MapInfo readJSON(String path){
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