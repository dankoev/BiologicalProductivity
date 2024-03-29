package com.RW.BiologicalProductivity.services.MapService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MapSector {
    public final static double noDataValue = -99999.0;
    private static  final Mat palitraHSV;
    private static final double kPalitra = 1.5;
    private static final int rowPalitra = 30;
    private static final int colskPalitra = 360;
    static {
        palitraHSV = createPalitraHSV(kPalitra);
    }

    
    public int offsetRows;
    public int offsetCols;
    public int rows;
    public int cols;
    public Mat data = new Mat();
    public boolean hasNoData = true;
    
    private int id;
    private double[][] cornerCoords = new double[2][2]; //left-top,right-bottom,
    
    private double maxMapValue = noDataValue;
    private double minMapValue= noDataValue;
    /**
     * optional Sector Info
     */
    private double maxSectorValue = noDataValue;
    private double minSectorValue = noDataValue;
    private double averageSectorValue = noDataValue;
    
    public double getMaxSectorValue() {
        return maxSectorValue;
    }
    
    public double getMinSectorValue() {
        return minSectorValue;
    }
    
    public double getAverageSectorValue() {
        return averageSectorValue;
    }
    
    public void setInitialData (int id, int offsetRows, int offsetCols,
                                int rows, int cols, double[][] cornerCoords,
                                boolean hasNoData){
        this.id = id; 
        this.offsetRows = offsetRows; 
        this.offsetCols = offsetCols; 
        this.rows = rows; 
        this.cols = cols; 
        this.cornerCoords = cornerCoords; 
        this.hasNoData = hasNoData; 
    }
    
    public void setMaxMinMapValue(double max, double min){
        this.minMapValue = min;
        this.maxMapValue = max;
    }
    
    public MapSector() {
    }
    
    public synchronized MapSector clone(){
        MapSector cloneSector =  new MapSector();
        cloneSector.setInitialData(id, offsetRows, offsetCols, rows, cols, cornerCoords, hasNoData);
        cloneSector.setMaxMinMapValue(maxMapValue, minMapValue);
        cloneSector.data = this.data.clone();
        return cloneSector;
    }
    public boolean calculateOptionalInfo(){
        Instant start = Instant.now();
        if ((this.maxSectorValue != noDataValue
                && this.minSectorValue != noDataValue
                && this.averageSectorValue != noDataValue) || hasNoData){
           return true;
        }
        if (data == null) {
            return false;
        }
        float[] buf = new float[cols*rows];
    
        data.get(0,0,buf);
        DoubleStream stream = IntStream.range(0, buf.length)
                .mapToDouble(i -> buf[i]);
        DoubleSummaryStatistics statistics = stream.filter(val -> val != noDataValue)
                .summaryStatistics();
    
        minSectorValue = statistics.getMin();
        maxSectorValue = statistics.getMax();
        averageSectorValue = statistics.getAverage();
    
        double format = 1E3;
        minSectorValue = Math.round(minSectorValue*format)/format;
        maxSectorValue = Math.round(maxSectorValue*format)/format;
        averageSectorValue = Math.round(averageSectorValue*format)/format;
        
        Instant finish = Instant.now();
        System.out.println("Время расчета SectorOptionalInfo "
                + Duration.between(start,finish).toMillis());
        
        return true;
        
    }
    public Mat toHeatMap(){
        Instant start = Instant.now();
        
        double[] RGBAClear = {0,0,0,0};
        Mat newImg = new Mat(this.rows, this.cols, CvType.CV_8UC4, new Scalar(RGBAClear));
        double[] valueRGB;
        int lenPalitraHSV = palitraHSV.cols();
        for (int i = 0; i < this.data.rows(); i++) {
            for (int j = 0; j < this.data.cols() ; j++) {
                if (this.data.get(i,j)[0] == noDataValue) {
                    continue;
                }
                if (this.data.get(i,j)[0] > maxMapValue){
                    valueRGB = palitraHSV.get(0,lenPalitraHSV-1);
                }
                else if(this.data.get(i,j)[0] < minMapValue){
                    valueRGB = palitraHSV.get(0,0);
                }
                else{
                    valueRGB = palitraHSV.get(0,(int)((lenPalitraHSV-1) * (this.data.get(i,j)[0] - minMapValue)/(maxMapValue - minMapValue)));
                }
                newImg.put(i, j, valueRGB[0],valueRGB[1],valueRGB[2], 255);
            }
        }
        
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Преобразование в тепловую карту , мс: " + elapsed);
        return newImg;
    }
    
    public double[] getMaxMinMapValue() {
        return new double[]{maxMapValue, minMapValue};
    }
    
    public static Mat createPalitraHSV(double c){
        double H = 0;
        double S = 0.78;
        double V = 0.88;
        double C,X,m;
        double[] _RGB;
        Mat mat = new Mat(rowPalitra,colskPalitra, CvType.CV_8UC3);
        for(int i = 0; i<30 ;i++)
            for(int j=0; j<360; j++)
            {
                H = j/c;
                C = V * S;
                X = C * (1 - Math.abs((H/60) % 2 - 1));
                m = V - C;
                if (H < 60){
                    _RGB = new double[] {C, X, 0};
                } else if ( H < 120) {
                    _RGB = new double[] {X, C, 0};
                } else if ( H < 180) {
                    _RGB = new double[] {0, C, X};
                } else if ( H < 240) {
                    _RGB = new double[] {0, X, C};
                } else if ( H < 300) {
                    _RGB = new double[] {X, 0, C};
                } else {
                    _RGB = new double[] {C, 0, X};
                }
                mat.put(i, j, (_RGB[0]+m) * 255.0, (_RGB[1]+m) * 255.0, (_RGB[2]+m) * 255.0);
            }
        return mat;
    }
    
}
