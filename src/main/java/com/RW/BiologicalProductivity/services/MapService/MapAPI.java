package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.models.PolygonInfo;
import com.RW.BiologicalProductivity.models.SectorsData;
import com.RW.BiologicalProductivity.models.SectorsInfo;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.models.MapInfo;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class MapAPI {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
        readMapInfo();
    }
    private static final String pathToMapsInfo = "./maps/region_1/info/";
    
    private static final String pathToMapHInfo = pathToMapsInfo + "mapHInfo.json";
    private static final String pathToMapCFTInfo = pathToMapsInfo + "mapCFTInfo.json";
    private static final String pathToMapNInfo = pathToMapsInfo + "mapNInfo.json";
    private static final String pathToMapT10Info = pathToMapsInfo + "mapT10Info.json";
    
    
    protected static MapInfo mapHinfo;
    protected static MapInfo mapCFTinfo;
    protected static MapInfo mapNinfo;
    protected static MapInfo mapT10info;
    
    private static void readMapInfo(){
        mapHinfo = readJSON(pathToMapHInfo);
        mapCFTinfo = readJSON(pathToMapCFTInfo);
        mapNinfo = readJSON(pathToMapNInfo);
        mapT10info = readJSON(pathToMapT10Info);
    }
    private static MapInfo readJSON(String path){
        try {
            Gson gson = new Gson();
            MapInfo mapInfo = gson.fromJson(new FileReader(path), MapInfo.class);
            System.out.println("JSON прочитан для карты: "+ mapInfo.name);
            return mapInfo;

        } catch (FileNotFoundException e) {
            System.out.println("File not exists: "+ path);
            return null;
        } catch (NumberFormatException e){
            System.out.println("Parse error: "+ path);
            return null;
        }
    }
    private static int  rowSplit = 8;
    private static int  colSplit = 8;
    public static Mat getHeatMap(int idSectors, TypeMap typeMap ) throws IOException{
       return getSector(idSectors, typeMap).toHeatMap();
    }
    public static byte [] getHeatMapAsBytes(int idSectors, TypeMap typeMap) throws IOException{
        
        Mat heatMap = getHeatMap(idSectors, typeMap);
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".jpeg",heatMap,buf);
        return buf.toArray();
    }
    public static byte [] calculPolygon(PolygonInfo info) throws IOException{
        Instant start = Instant.now();
        Resource sectorRes = new FileSystemResource("sectors/region_1/data/8/ZM/sector_26.jpeg");
        byte[] sector = Files.readAllBytes(sectorRes.getFile().toPath());
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);
        return sector;
    }
    public static byte[] getHeatMapByPath (String path) throws IOException {
        Resource sectorRes = new FileSystemResource(path);
        return Files.readAllBytes(sectorRes.getFile().toPath());
    }
    public static  SectorsInfo getSectorsInfoByPolygon(PolygonInfo polyInfo) throws IOException{
        Gson gson = new Gson();
        SectorsInfo info = gson.fromJson(new FileReader("sectors/sectorsInfoTest.json"), SectorsInfo.class);
        
        return info;
    }
//    private List<SectorsData> check(PolygonInfo polyInfo,SectorsInfo sectorsInfo){
//        polyInfo.getCoordinates().stream().map()
//    }
//    private boolean checkEntry(SectorsData sectorsData,List<Double[]> polyCoords){
//        double[][] sectorCoords = sectorsData.getCornerCoords();
//        for (int i = 0; i < s; i++) {
//
//        }
//
//    }
    public static MapSector getSector(int idSectors, TypeMap typeMap) throws IOException {
        MapsManipulationImpl manipulation = new MapsManipulationImpl(mapHinfo, mapCFTinfo,mapNinfo, mapT10info,rowSplit,colSplit);
        return  manipulation.getSectorById(idSectors,typeMap);
    }
    public static void setRowSplit(int rowSplit) {
        MapAPI.rowSplit = rowSplit;
    }
    public static void setColSplit(int colSplit) {
        MapAPI.colSplit = colSplit;
    }
}
