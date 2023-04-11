package com.RW.BiologicalProductivity.services.MapService.Deployment.interfaces;

import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;

public abstract class MapDeployment {
    protected final RegionService regionService;
    protected final Region region;
    public abstract void deployMap(TypeMap typeMap) throws IOException, InterruptedException;
    
    public MapDeployment(RegionService regionService, String regionName) {
        this.regionService = regionService;
        this.region = regionService.getInfo(regionName);
    }
    
    
    
    protected void checkInfo(TypeMap typeMap){
        boolean mapHas = regionService.getMapsInfo(region.getName())
                .stream()
                .anyMatch(info ->  info.getType() == typeMap);
        if(mapHas){
            throw new Error("MapDeployment: ошибка развертывания карты, тип карты"
                    + typeMap.name() + "существует");
        }
    }
    public boolean writeDeployingMap(Mat deployingMap, TypeMap typeMap){
        Imgcodecs.imwrite(region.getMapsRootPath() + "/" +
                typeMap.name() + ".tif",deployingMap,new MatOfInt(Imgcodecs.IMWRITE_TIFF_COMPRESSION,0));
        //Проверка на успешную загрузку
        return false;
    }
}
