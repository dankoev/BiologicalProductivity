package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MapUploadService {
    private static final String defaultPathToMaps= "./maps/";
    
    
    public RegionService regionService;
    public MapInfoService mapInfoService;
    
    public MapUploadService(RegionService regionService,
                            MapInfoService mapInfoService) {
        this.regionService = regionService;
        this.mapInfoService = mapInfoService;
    }
    
    public void checkMapsDirectory() throws IOException {
        File mapsDir = new File(defaultPathToMaps);
        if ( !mapsDir.exists() || !mapsDir.isDirectory() ){
            throw new IOException(MapUploadService.class + "Не существует директри с картами");
        }
        for (File item: Objects.requireNonNull(mapsDir.listFiles())){
            if (item.isDirectory()
                && !regionService.hasInfo(item.getName())
                && Objects.requireNonNull(item.listFiles()).length > 0){
                AddNotesAboutMap(item);
            }
            
        }
    }
    private void AddNotesAboutMap(File srcDir) throws IOException {
        System.out.println(this.getClass() + ": Adding notes...");
        double[][] latLongCoords = null;
        Region region = new Region(srcDir.getName(),srcDir.getPath());
        
        for (File item: Objects.requireNonNull(srcDir.listFiles())) {
            TypeMap typeMap = TypeMap.getTypeByName(item.getName());
            if (typeMap == null){
                System.out.println(this.getClass() + ": Ошибка карта " + item.getName() + " не загружена");
            }
            GdalService gdalService = new GdalService(item.getPath());
            if (latLongCoords == null){
                latLongCoords = gdalService.getLatLongCoords();
            }
            double[] minMaxValue = gdalService.getMinMax(1);
            MapInfo mapInfo =  new MapInfo(item.getName(),typeMap,minMaxValue[0],minMaxValue[1]);
            region.addMapsInfo(mapInfo);
        }
        
        region.setTopLat(latLongCoords[1][0]);
        region.setBottomLat(latLongCoords[1][1]);
        region.setLeftLong(latLongCoords[0][0]);
        region.setRightLong(latLongCoords[0][1]);
        regionService.save(region);
        
        
        
        
        
    }
    
}
