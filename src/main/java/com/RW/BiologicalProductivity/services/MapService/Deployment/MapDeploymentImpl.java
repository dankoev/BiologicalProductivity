package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.MapApiDeployment;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.MapsManipParall;
import com.RW.BiologicalProductivity.services.MapService.MapsManipWithMapData;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;
import java.util.NoSuchElementException;

public class MapDeploymentImpl extends MapDeployment {
    
    public MapDeploymentImpl(RegionService regionService, MapInfoService mapInfoService, String regionName) throws NoSuchValueException {
        super(regionService, mapInfoService, regionName);
    }
    
    //Handle Error
    @Override
    public void deployMap(TypeMap typeMap) throws IOException, DataBaseException {
        checkInfo(typeMap);
        MapAPI api = new MapApiDeployment(new MapsManipWithMapData(
                regionService.getMapsInfo(region.getName()),
                rowSplit,
                colSplit));
        
        Mat deployingMap = new Mat(region.getRegionRows(), region.getRegionCols(), region.getCvType());
        for (int i = 0; i < getNumberSectors(); i++) {
            MapSector sector = api.getSector(i,typeMap);
            Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                    sector.cols,sector.rows);
            Mat roi = deployingMap.submat(rect);
            sector.data.copyTo(roi);
        }
        writeDeployingMap(deployingMap,typeMap);
        addNoteToDB(typeMap);
    }
    
    
    

    


    
}
