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
    public static String pathToMapHInfo ="mapsInfo/mapHData.json";

    public static void main(String[] args) {
        MapInfo mapHinfo = readJSON(pathToMapHInfo);
        MapData mapData = new MapData(mapHinfo.pathToMap, mapHinfo.cornerCoords, mapHinfo.maxMapValue, mapHinfo.minMapValue);
        MapSector  sector = mapData.getFillSector(26);

        HighGui.namedWindow("Input", HighGui.WINDOW_AUTOSIZE);

        Mat newMat = sector.sectorToRGB();
        HighGui.imshow("Input", newMat);
        HighGui.waitKey();
        
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