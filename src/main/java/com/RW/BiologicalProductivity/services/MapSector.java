package com.RW.BiologicalProductivity.services;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MapSector {
    public final static double noDataValue = -99999.0;
    private static Mat palitraHSV = null;
    private static final double kPalitra = 1.5;
    private static final int rowPalitra = 30;
    private static final int colskPalitra = 360;
    

    public int id;
    public int offsetRows;
    public int offsetCols;
    public int rows;
    public int cols;
    public double[][] cornerCoords = new double[4][2]; //left-top, right-top,right-bottom,left-bottom
    public boolean hasNoData = true;

    public double maxMapValue = noDataValue;
    public double minMapValue= noDataValue;
    public List<MapElementData> mapDataList = new ArrayList<>();
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
        cloneSector.mapDataList.addAll(this.mapDataList);
        return cloneSector;
    }
    
    public Mat toHeatMap(){
        try {
            double[] RGBWhite = {255,2555,255};
            Scalar initialRGB = new Scalar(RGBWhite);
            Mat newImg = new Mat(this.rows, this.cols, CvType.CV_8UC3, initialRGB);
            double[] valueRGB;
            if (palitraHSV == null)
                palitraHSV = createPalitraHSV(kPalitra);
            int lenPalitraHSV = palitraHSV.cols();
            for (MapElementData el : this.mapDataList){
                if (!el.isNoData) {
                    if (el.value > maxMapValue){
                        valueRGB = palitraHSV.get(0,lenPalitraHSV-1);
                    }
                    else if(el.value < minMapValue){
                        valueRGB = palitraHSV.get(0,0);
                    }
                    else{
                        valueRGB = palitraHSV.get(0,(int)((lenPalitraHSV-1) * (el.value - minMapValue)/(maxMapValue - minMapValue)));
                    }
                    if (valueRGB != null)
                        newImg.put(el.x- this.offsetRows, el.y-this.offsetCols, valueRGB);
                    else
                        System.out.println("stop");
                }
                else {
                    newImg.put(el.x- this.offsetRows, el.y-this.offsetCols, RGBWhite);
                }
            }
            return newImg;
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            System.out.println("Error of map generation!! ");
            return null;
        }
        
    }
    public static Mat createPalitraHSV(double c){
        double H = 0;
        double S = 0;
        double V = 0;
        double C,X,m;
        double[] _RGB;
        Mat mat = new Mat(rowPalitra,colskPalitra, CvType.CV_8UC3);
        for(int i = 0; i<30 ;i++)
            for(int j=0; j<360; j++)
            {
                H = j/c;
                S = 0.78;
                V = 0.88;

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
