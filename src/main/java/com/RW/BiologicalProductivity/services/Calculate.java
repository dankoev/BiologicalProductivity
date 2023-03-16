package com.RW.BiologicalProductivity.services;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.RW.BiologicalProductivity.services.enums.TypeMap;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.RW.BiologicalProductivity.services.models.MapInfo;
import com.google.gson.Gson;

class Calculate {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
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
    
}