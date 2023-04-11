package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MapApiWithMapData implements MapAPI {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
    }
    
    private final Region region;
    private List<MapInfo> mapsInfo;
    private MapsManipulation manipulation;
    private final RegionService regionService;
    private int rowSplit = 8;
    private int colSplit = 8;
    private int numberSectors = colSplit * rowSplit;
    
    public MapApiWithMapData(String regionName,RegionService regionService) {
        this.regionService = regionService;
        this.region = regionService.getInfo(regionName);
        
    }
    public void startAPI() throws IOException {
        MapData mapDataH = new MapData(getMapInfo(TypeMap.H),rowSplit,colSplit);
        MapData mapDataCFT = new MapData(getMapInfo(TypeMap.CFT),rowSplit,colSplit);
        MapData mapDataN = new MapData(getMapInfo(TypeMap.N),rowSplit,colSplit);
        MapData mapDataT = new MapData(getMapInfo(TypeMap.T),rowSplit,colSplit);
        this.manipulation = new MapsManipWithMapData(mapDataH,mapDataCFT,mapDataT,mapDataN);
    }
    private MapInfo getMapInfo(TypeMap typeMap){
        if (mapsInfo == null)
            mapsInfo = regionService.getMapsInfo(region.getName());
        return mapsInfo.stream()
                .filter(info -> info.getType() == typeMap)
                .findFirst()
                .orElseThrow();
    }
    public  MapSector getSector(int idSectors, TypeMap typeMap) throws IOException {
        return  manipulation.getSectorById(idSectors,typeMap);
    }
    public  void setRowSplit(int rowSplit) {
        this.rowSplit = rowSplit;
        numberSectors = colSplit * rowSplit;
    }
    public  void setColSplit(int colSplit) {
        this.colSplit = colSplit;
        numberSectors = colSplit * rowSplit;
    }
    
    public int getNumberSectors() {
        return numberSectors;
    }
}
