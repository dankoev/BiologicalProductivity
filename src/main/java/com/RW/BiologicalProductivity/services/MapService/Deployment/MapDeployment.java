package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.models.RegionInfo;
import com.RW.BiologicalProductivity.models.SectorsData;
import com.RW.BiologicalProductivity.models.SectorsInfo;
import com.RW.BiologicalProductivity.services.MapService.Deployment.enums.MapSplitSize;
import com.RW.BiologicalProductivity.services.MapService.Deployment.enums.SectorsScope;
import com.RW.BiologicalProductivity.services.MapService.MapAPI;
import com.RW.BiologicalProductivity.services.MapService.MapData;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;

public class MapDeployment{
    
    private static final String defaultPathToMaps= "./maps/";
    private static final String defaultPathToSectors= "./sectors/";
    private static final String pathToSectors= defaultPathToSectors;
    private static final String pathToMaps = defaultPathToMaps;
//    private static RegionInfo regionInfo;
    
    public static void main(String[] args) throws IOException {
        createSectors("region_1",SectorsScope.SCOPE_EIGHT,MapSplitSize.EIGHT_BY_EIGHT);
    }
    /** Write new maps*/
    public boolean writeMaps(String region){
        return true;
    }
    
    public static void createSectors(String region, SectorsScope scope, MapSplitSize splitSize) throws IOException {
        
//        if(readRegionInfo(region)){
//            File dir = new File(defaultPathToSectors + regionInfo.name);
//            boolean created = dir.mkdir();
//            if(!created){
//                System.out.println("Не удалось создать директорию " +
//                        "для сектров, проверьте установленный путь: " + pathToSectors);
//            }
//            File dataDir = new File(dir,"data");
//            File infoDir = new File(dataDir,"data");
//
//
//        };
        
        if (scope.equals(SectorsScope.SCOPE_EIGHT) && splitSize.equals(MapSplitSize.EIGHT_BY_EIGHT)){
            String currentPath = "sectors/region_1/data/8/";
            currentPath = currentPath + TypeMap.BP.name();
            SectorsInfo sectorsInfo = new SectorsInfo(TypeMap.BP.name(),8,"sectors/region_1/data/8/ZM");
            for (int i = 0; i < 64; i++) {
                Instant start = Instant.now();
                MapSector sector =  MapAPI.getSector(i, TypeMap.BP);
//                Imgcodecs.imwrite(currentPath + "/sector_" + i +
//                        ".jpeg" ,mat);
                SectorsData data = new SectorsData("sector_" + i + ".jpeg",sector.cornerCoords);
                sectorsInfo.putSectorData(data);
                Instant end  = Instant.now();
                System.out.println("Время выполнения " + Duration.between(start,end).toMillis());
            }

            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.writeValue(new FileWriter("sectors/region_1/data/8/ZM/sectorsInfoTest.json"),sectorsInfo);
        }
        
        
    }
    public static void createRegionInfo(){
    }
//    private static  boolean readRegionInfo(String region){
//        try {
//            Gson gson = new Gson();
//            RegionInfo regionInfo = gson.fromJson(new FileReader(pathToMaps+region), RegionInfo.class);
//            if(!regionInfo.hasData){
//                System.out.println("региона с именем '" + region + "' не существует");
//                return false;
//            }
//            regionInfo = regionInfo;
//            return true;
//
//        }catch (IOException e){
//            System.out.println("Ошибка чтения файла региона");
//        }catch (JsonParseException e){
//            System.out.println("Ошибка чтения JSON региона");
//        }
//        return false;
//    }

    
}
