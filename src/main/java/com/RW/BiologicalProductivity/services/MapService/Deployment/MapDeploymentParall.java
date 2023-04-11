package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.interfaces.MapDeployment;
import com.RW.BiologicalProductivity.services.MapService.MapApiWithMapData;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapDeploymentParall extends MapDeployment {
    private volatile MapApiWithMapData api;
    private volatile Mat deployingMap;
    private volatile TypeMap typeMap;
    
    public MapDeploymentParall(RegionService regionService, String regionName) {
        super(regionService,regionName);
    }
    
    @Override
    public void deployMap(TypeMap typeMap) throws IOException, InterruptedException {
        checkInfo(typeMap);
        
        this.api = new MapApiWithMapData(region.getName(),regionService);
        this.deployingMap = new Mat(region.getRegionRows(), region.getRegionCols(), region.getCvType());
        this.typeMap = typeMap;
        api.startAPI();
        
        List<DeployTread> treads = new ArrayList<>(api.getNumberSectors());
        for (int i = 0; i < api.getNumberSectors(); i++) {
            treads.add(new DeployTread(this,i));
        }
        
        treads.forEach(Thread::start);
        for (DeployTread tread : treads) {
            tread.join();
        }
        writeDeployingMap(deployingMap,typeMap);
    }
    
    public void deploySector(int sectorId) throws IOException {
        MapSector sector = api.getSector(sectorId,typeMap);
        Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                sector.cols,sector.rows);
        Mat roi = deployingMap.submat(rect);
        sector.data.copyTo(roi);
    }
}
