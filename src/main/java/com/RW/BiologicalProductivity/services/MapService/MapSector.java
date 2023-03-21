package com.RW.BiologicalProductivity.services.MapService;

import java.util.ArrayList;
import java.util.List;

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

    public int id;
    public int offsetRows;
    public int offsetCols;
    public int rows;
    public int cols;
    public double[][] cornerCoords = new double[4][2]; //left-top, right-top,right-bottom,left-bottom
    public boolean hasNoData = true;
    public Mat data = new Mat();

    public double maxMapValue = noDataValue;
    public double minMapValue= noDataValue;
    public void setInitialData (int id,int offsetRows,int offsetCols,
                            int rows,int cols,double[][] cornerCoords,
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
    
    public MapSector clone(){
        MapSector cloneSector =  new MapSector();
        cloneSector.setInitialData(id, offsetRows, offsetCols, rows, cols, cornerCoords, hasNoData);
        cloneSector.setMaxMinMapValue(maxMapValue, minMapValue);
        cloneSector.data = this.data.clone();
        return cloneSector;
    }
    
    public Mat toHeatMap(){
        double[] RGBWhite = {255,2555,255};
        Scalar initialRGB = new Scalar(RGBWhite);
        Mat newImg = new Mat(this.rows, this.cols, CvType.CV_8UC3, initialRGB);
        double[] valueRGB;
        int lenPalitraHSV = palitraHSV.cols();
        for (int i = 0; i < this.data.rows(); i++) {
            for (int j = 0; j < this.data.cols() ; j++) {
                if (this.data.get(i,j)[0] == noDataValue) {
                    newImg.put(i, j, RGBWhite);
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
                newImg.put(i, j, valueRGB);
            }
        }
        return newImg;
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
