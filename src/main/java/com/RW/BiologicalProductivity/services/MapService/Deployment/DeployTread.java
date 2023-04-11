package com.RW.BiologicalProductivity.services.MapService.Deployment;

import com.RW.BiologicalProductivity.services.MapService.Deployment.interfaces.MapDeployment;
import com.RW.BiologicalProductivity.services.MapService.MapSector;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;

public class DeployTread extends Thread{
    private final MapDeploymentParall mapDeployment;
    private final int sectorId;
    
    public DeployTread(MapDeploymentParall mapDeployment, int sectorId) {
        this.mapDeployment = mapDeployment;
        this.sectorId = sectorId;
    }
    
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" start");
        try {
            this.mapDeployment.deploySector(sectorId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" dead");
    }

}
