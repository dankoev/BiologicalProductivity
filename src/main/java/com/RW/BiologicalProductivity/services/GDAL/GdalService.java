package com.RW.BiologicalProductivity.services.GDAL;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.IOException;

public class GdalService {
    private Dataset hDataset;
    private double[] transform;
    private int rasterCount;
    
    public GdalService(String pathToMap) throws IOException {
        gdal.AllRegister();
        this.hDataset = gdal.Open(pathToMap, gdalconstConstants.GA_ReadOnly);
        if (hDataset == null){
            throw new IOException("GDAl: Error read file");
        }
        rasterCount = hDataset.getRasterCount();
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
    public double[] getMinMax(int iBand){
        Double[] min = new Double[1], max = new Double[1];
        double[] minMax = new double[2];
        Band hBand = hDataset.GetRasterBand(iBand);
        hBand.GetMaximum(max);
        hBand.GetMinimum(min);
        if (min[0] == null || max[0] == null){
            hBand.ComputeRasterMinMax(minMax);
            return minMax;
        }
        minMax[0] = min[0];
        minMax[1] = max[0];
        return minMax;
    }
    
}
