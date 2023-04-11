package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.interfaces.MapDeployment;
import com.RW.BiologicalProductivity.services.MapService.MapApiWithMapData;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;

public class MapDeploymentImpl extends MapDeployment {
    
    public MapDeploymentImpl(RegionService regionService, String regionName) {
        super(regionService,regionName);
    }
    
    @Override
    public void deployMap(TypeMap typeMap) throws IOException {
        checkInfo(typeMap);
        MapApiWithMapData api = new MapApiWithMapData(region.getName(),regionService);
        api.startAPI();
        
        Mat deployingMap = new Mat(region.getRegionRows(), region.getRegionCols(), region.getCvType());
        for (int i = 0; i < api.getNumberSectors(); i++) {
            MapSector sector = api.getSector(i,typeMap);
            Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                    sector.cols,sector.rows);
            Mat roi = deployingMap.submat(rect);
            sector.data.copyTo(roi);
        }
        writeDeployingMap(deployingMap,typeMap);
    }
    private  boolean addNoteToDB(){
        return false;
    }
    
    


    
}
