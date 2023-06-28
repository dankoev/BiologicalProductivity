package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.exceptions.OverwriteAttemptException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.opencv.core.Mat;

import java.io.IOException;

public abstract class MapDeployment {
    protected final RegionService regionService;
    private final MapInfoService mapInfoService;
    protected final Region region;
    
    protected int rowSplit = 8;
    protected int colSplit = 8;
    protected int numberSectors = colSplit * rowSplit;
    private static final double noDataValue = -99999.0;
    public abstract void deployMap(TypeMap typeMap) throws IOException, InterruptedException, DataBaseException;
    
    public MapDeployment(RegionService regionService, MapInfoService mapInfoService, String regionName) throws NoSuchValueException {
        this.regionService = regionService;
        this.mapInfoService = mapInfoService;
        this.region = regionService.getInfo(regionName);
    }
    
    protected void checkInfo(TypeMap typeMap) throws NoSuchValueException, OverwriteAttemptException {
        boolean mapHas = regionService.getMapsInfo(region.getName())
                .stream()
                .anyMatch(info ->  info.getType() == typeMap);
        if(mapHas){
           throw new OverwriteAttemptException("Error map deploy. Map with type '" + typeMap + "' already has deployed");
        }
    }
    public void   writeDeployingMap(Mat deployingMap, TypeMap typeMap) throws IOException {
        int hasWrote = GdalService.writeMap(deployingMap,region.getMapsRootPath() + "/" +
                typeMap.name() + ".tif",noDataValue, region.getTransform());
        if (hasWrote != 0){
            throw new IOException("Error write raster");
        }
//        Imgcodecs.imwrite(region.getMapsRootPath() + "/" +
//                typeMap.name() + ".tif",deployingMap,new MatOfInt(Imgcodecs.IMWRITE_TIFF_COMPRESSION,0));
    }
    
    protected void addNoteToDB(TypeMap typeMap){
        mapInfoService.saveNotesAboutMap(typeMap,region);
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
