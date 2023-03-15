package com.RW.BiologicalProductivity.services;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.IOException;

public class GdalService {
    private Dataset hDataset;
    private double[] transform;
    
    public GdalService(String pathToMap) throws IOException {
        gdal.AllRegister();
        this.hDataset = gdal.Open(pathToMap, gdalconstConstants.GA_ReadOnly);
        if (hDataset == null){
            throw new IOException("error read file");
        }
        transform = hDataset.GetGeoTransform();
    }
    public double[] getTransform(){
        return transform;
    }
    public double[] getGeoCoordByPixels(int row, int col){
        double x = transform[0] + col * transform[1] + row * transform[2];
        double y = transform[3] + col * transform[4] + row * transform[5];
        return new double[]{x,y};
    }
    
}
