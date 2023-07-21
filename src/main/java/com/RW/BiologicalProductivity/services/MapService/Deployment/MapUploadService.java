package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.exceptions.UnknownDbException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class MapUploadService {
    private static final String defaultPathToMaps= "./maps/";
    public static final  int typeMapCodding = 5;
    
    public RegionService regionService;
    public MapInfoService mapInfoService;
    
    public MapUploadService(RegionService regionService,
                            MapInfoService mapInfoService) {
        this.regionService = regionService;
        this.mapInfoService = mapInfoService;
    }
    
    public void checkMapsDirectory() throws IOException, UnknownDbException {
        File mapsDir = new File(defaultPathToMaps);
        if ( !mapsDir.exists() || !mapsDir.isDirectory() ){
            throw new IOException("No such directory with maps. Create 'maps' dir in root");
        }
        File [] regionsDir = Objects.requireNonNull(mapsDir.listFiles());
        if (regionsDir.length == 0) {
            throw new IOException("There is no information about regions. Create a folder with files in the maps directory");
        }
        for (File item: regionsDir){
            if (item.isDirectory()
                && Objects.requireNonNull(item.listFiles()).length > 0){
                try {
                    cleanNotesInRegion(item);
                    AddNotesAboutRegion(item);
                }catch (NoSuchValueException e){
                    throw new UnknownDbException("Ошибка БД");
                }
                
            }
            
        }
    }
    public void cleanNotesInRegion(File srcDir) throws NoSuchValueException {
        if (!regionService.hasInfo(srcDir.getName())){
            System.out.printf("skip clean mapdir");
            return;
        }
        List<MapInfo> mapsInfo = regionService.getMapsInfo(srcDir.getName());
        for (MapInfo mapInfo: mapsInfo) {
            File tiff = new File(srcDir.getPath() + "/" + mapInfo.getName());
            if (!tiff.exists()){
                System.out.printf("delete " + tiff.getPath() );
                regionService.deleteMapInfoById(srcDir.getName(), mapInfo.getId());
            }
        }
    }
    private void AddNotesAboutMap(Region region,File srcDir) throws IOException{
        TypeMap typeMap = TypeMap.getTypeByName(srcDir.getName());
        if (typeMap == null){
            System.out.println("MapUploadService: Не удалось загрузить карту " + srcDir.getName()+": проверьте название");
            return;
        }
        boolean has_info = region.getMapsInfo()
                .stream()
                .anyMatch((mapInfo) -> mapInfo.getType() == typeMap);
        if (has_info){
            System.out.println("MapUploadService: Карта " + srcDir.getName()+ " не загружена: запись о такой карте уже существует");
            return;
        }
        GdalService gdalService = new GdalService(srcDir.getPath());
        if (!region.isFilled() && TypeMap.isInitialMap(typeMap)) {
            double[][] latLongCoords = gdalService.getLatLongCoords();
            region.setTopLat(latLongCoords[1][0]);
            region.setBottomLat(latLongCoords[1][1]);
            region.setLeftLong(latLongCoords[0][0]);
            region.setRightLong(latLongCoords[0][1]);
            //rows and cols
            int[] size  = gdalService.getXYSize();
            region.setRegionRows(size[1]);
            region.setRegionCols(size[0]);
            region.setTransform(gdalService.getTransform());
            region.setCvType(typeMapCodding);
            region.setFilled(true);
            
        }
        MapInfo mapInfo =  new MapInfo(srcDir.getName(),typeMap);
        region.addMapsInfo(mapInfo);
        
    }
    private void AddNotesAboutRegion(File srcDir) throws IOException, NoSuchValueException {
        System.out.println(this.getClass() + ": Adding notes...");
        
        Region region;
        if (regionService.hasInfo(srcDir.getName())){
            region = regionService.getFullInfo(srcDir.getName());
        } else {
            region = new Region(srcDir.getName(),srcDir.getPath());
        }
        
        for (File item: Objects.requireNonNull(srcDir.listFiles())) {
            AddNotesAboutMap(region,item);
        }
        regionService.save(region);
        
    }
    
}
