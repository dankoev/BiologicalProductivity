package com.RW.BiologicalProductivity.services.GDAL;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class GdalService {
    private Dataset hDataset;
    private double[] transform;
    private int rasterCount;
    
    public GdalService() {
    }
    
    public GdalService(String pathToMap) throws IOException {
        gdal.AllRegister();
        this.hDataset = gdal.Open(pathToMap, gdalconstConstants.GA_ReadOnly);
        if (hDataset == null){
            throw new IOException("GDAl: Error read file");
        }
        rasterCount = hDataset.getRasterCount();
        transform = hDataset.GetGeoTransform();
    }
    public int[] getXYSize(){
        final int[] size = new int[]{
                hDataset.getRasterXSize(),
                hDataset.getRasterYSize()
        };
        return size;
    };
    
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
    public double[][] getLatLongCoords(){
        double[] TLpoint = this.getGeoCoordByPixels (0, 0);
        double[] BRpoint = this.getGeoCoordByPixels (hDataset
                .getRasterYSize(), hDataset.getRasterXSize());
        return new double[][] {{TLpoint[0],BRpoint[0]},{TLpoint[1],BRpoint[1]}};
    }
    

    public Rect getRectByLatsLongs(double[][] coords ){
        Point startPoint = getPointByWords(coords[0][0], coords[0][1]);
        Point endPoint = getPointByWords(coords[1][0], coords[1][1]);
        return new Rect(startPoint, endPoint);
    }
    
    public Point getPointByWords(double longitude, double latitude){
        int row = (int) Math.round((latitude - transform[3]) / transform[5]);
        int col = (int) Math.round((longitude - transform[0]) / transform[1]);
        return new Point(col,row);
    }
    public static int writeMap(Mat map, String path,double noDataVal, double[] transform){
        ogr.RegisterAll();
        int cols = map.cols();
        int rows = map.rows();
        float[] buf = new float[cols*rows];
        
        map.get(0,0,buf);
        DoubleStream stream = IntStream.range(0, buf.length)
                .mapToDouble(i -> buf[i]);
        DoubleSummaryStatistics statistics = stream.filter(val -> val != noDataVal)
                .summaryStatistics();

        double min = statistics.getMin();
        double max = statistics.getMax();
        double  average = statistics.getAverage();
        double count = statistics.getCount();
        double finalAverage = average;
        double stddev = Math.sqrt(IntStream.range(0, buf.length)
                .mapToDouble(i -> buf[i])
                .filter(val -> val != noDataVal)
                .map(val -> Math.pow((val - finalAverage),2))
                .sum() / count);

        double format = 1E4;
        min = Math.round(min*format)/format;
        max = Math.round(max*format)/format;
        average = Math.round(average*format)/format;
        stddev = Math.round(stddev*format)/format;
        
        Driver driver = gdal.GetDriverByName("GTiff");
        Dataset ds = driver.Create(path,
                cols,rows, 1, gdalconstConstants.GDT_Float32);
        SpatialReference src = new SpatialReference();
        src.ImportFromEPSG(4326);
        ds.SetProjection(src.ExportToWkt());
        ds.SetGeoTransform(transform);
        Band out = ds.GetRasterBand(1);
        
        
        out.SetStatistics(min,max,average,stddev);
        out.SetNoDataValue(noDataVal);
        
        int hasWrote = out.WriteRaster(0,0,cols,rows,buf);
        out.FlushCache();
        out.delete();
        return hasWrote;
        
    }
}
