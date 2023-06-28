package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.*;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class MapDeploymentParall extends MapDeployment {
    private volatile MapApiDeployment api;
    private volatile Mat deployingMap;
    private volatile TypeMap typeMap;
    
    public MapDeploymentParall(RegionService regionService, MapInfoService mapInfoService, String regionName) throws NoSuchValueException {
        super(regionService, mapInfoService, regionName);
    }
    
    
    //Handle Error
    @Override
    public void deployMap(TypeMap typeMap) throws IOException, InterruptedException, DataBaseException {
        checkInfo(typeMap);
        
        this.api = new MapApiDeployment(new MapsManipWithMapData(
                regionService.getMapsInfo(region.getName()),
                rowSplit,
                colSplit));
        this.deployingMap = new Mat(region.getRegionRows(), region.getRegionCols(), region.getCvType());
        this.typeMap = typeMap;
        
        List<DeployTread> treads = new ArrayList<>(getNumberSectors());
        for (int i = 0; i < getNumberSectors(); i++) {
            treads.add(new DeployTread(this,i));
        }
        
        treads.forEach(Thread::start);
        for (DeployTread tread : treads) {
            tread.join();
        }
        writeDeployingMap(deployingMap,typeMap);
        addNoteToDB(typeMap);
    }
    
    public void deploySector(int sectorId) throws IOException {
        MapSector sector = api.getSector(sectorId,typeMap);
        Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                sector.cols,sector.rows);
        Mat roi = deployingMap.submat(rect);
        sector.data.copyTo(roi);
    }
}
