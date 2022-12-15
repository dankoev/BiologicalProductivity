package com.RW.BiologicalProductivity.services;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class MapData {
    private static final double noDataValue = -99999.0;
    public static final  int typeMapCoding = Imgcodecs.IMREAD_UNCHANGED | 
                                             Imgcodecs.IMREAD_ANYDEPTH |
                                             Imgcodecs.IMREAD_LOAD_GDAL;
    
    private Mat img;
    private int imgCols = 0;
    private int imgRows = 0;

    public double maxMapValue = 0;
    public double minMapValue = 0;

    public double[] upLeftCoords;
    public double[] lowerLeftCoords;
    public double[] upRightCoords;
    public double[] lowerRightCoords;
    
    public int nSectors = 64;
    public List<MapSector> sectors = new ArrayList<>();


    public MapData(String pathToMap,
                    double[][] cornerCoords,/*left-top, right-top,right-bottom,left-bottom */
                    double maxMapValue,
                    double minMapValue){
        this.img = Imgcodecs.imread(pathToMap, typeMapCoding);
        this.imgCols = img.cols();
        this.imgRows = img.rows();

        this.upLeftCoords = cornerCoords[0];
        this.lowerLeftCoords = cornerCoords[3];
        this.upRightCoords = cornerCoords[1];
        this.lowerRightCoords = cornerCoords[2];
        
        this.maxMapValue = maxMapValue;
        this.minMapValue = minMapValue;
        countSectors();
    }
    public  Mat getGrayMap(double minMapValue,double maxMapValue){
        Mat newImg = new Mat(imgRows, imgCols, CvType.CV_8U);
        int newvValue;
        double currValue;
        for (int x = 0; x < imgRows; x++) {
            for (int y = 0; y < imgCols; y++) {
                currValue = img.get(x,y)[0];
                newvValue = (int)(255 * (currValue - minMapValue) / maxMapValue);
                newImg.put(x, y , newvValue);
            }
            
        }
        return newImg;
    }
    private double[][] calcCornWordsCoord(int offsetRows, int offsetCols, int rows, int cols){
        double[][] cornerCoords = new double[4][2];
        cornerCoords[0] = pixelToWord(offsetRows,offsetCols);
        cornerCoords[1] = pixelToWord(offsetRows,offsetCols + cols);
        cornerCoords[2] = pixelToWord(offsetRows + rows,offsetCols + cols);
        cornerCoords[3] = pixelToWord(offsetRows  + rows,offsetCols);
        return cornerCoords;

    }
    private void countSectors(){
        int id = 0;
        boolean hasNoData;
        int offsetRows,offsetCols,cols,rows;
        double[][] cornerCoords;
        for( int i = 0; i < Math.sqrt(nSectors); i++) {
            for (int j = 0; j < Math.sqrt(nSectors); j++) {
                MapSector sector = new MapSector();
                offsetRows = (int)(imgRows / Math.sqrt(nSectors) * i);
                offsetCols = (int) (imgCols / Math.sqrt(nSectors) * j);
                cols = (int) (imgCols / Math.sqrt(nSectors) * (j+1)) - offsetCols;
                rows = (int)(imgRows / Math.sqrt(nSectors) * (i+1)) - offsetRows;
                cornerCoords = calcCornWordsCoord(offsetRows,offsetCols,rows,cols);
                hasNoData = detectNoDataSectors(offsetRows,offsetCols,rows,cols);

                sector.setInitialData(id, offsetRows, offsetCols, rows, cols, cornerCoords,hasNoData);
                sector.setMaxMinMapValue(maxMapValue, minMapValue);
                this.sectors.add(sector);
                id++;
            }
        }
    }
    private boolean detectNoDataSectors(int offsetRows, int offsetCols, int rows, int cols){
        for (int x = offsetRows; x < offsetRows + rows; x++){

            if(this.img.get(x,offsetCols)[0] != noDataValue ||
                    this.img.get(x,offsetCols + cols-1)[0] != noDataValue ){

                    return false;
            }
        }
        for (int y = offsetCols; y < offsetCols + cols; y++){

            if(this.img.get(offsetRows,y)[0] != noDataValue ||
                    this.img.get(offsetRows+rows-1,y)[0] != noDataValue ){

                     return false;
            }
        }
        return true;

    }
    public MapSector getFillSector(int idSector){
        MapSector sector = new MapSector();
        try {
            sector = sectors.get(idSector).clone();
        } catch (CloneNotSupportedException e) {
           System.out.println("Ошибка клонирования сектора");
        }

        if (sector.hasNoData|| !sector.mapDataList.isEmpty())
            return sector;
        double value;
        double[] wordCoordinate;
        for (int x = sector.offsetRows; x < sector.offsetRows + sector.rows; x++){
            for ( int y = sector.offsetCols; y < sector.offsetCols + sector.cols; y++) {
                value = img.get(x,y)[0];
                wordCoordinate = pixelToWord(x,y);
                MapElementData mapData =  new MapElementData();
                mapData.setData(value,x,y,wordCoordinate);
                sector.mapDataList.add(mapData);
            }
        }
        return sector;
    }
    private double[] pixelToWord(int x, int y){
        double rx = (double)x / imgRows;
        double ry = (double)y / imgCols;

        double[] rightSide = this.lerp(upRightCoords, lowerRightCoords, rx);
        double[] leftSide  = this.lerp(upLeftCoords, lowerLeftCoords, rx);
        
        return lerp( leftSide, rightSide, ry );
    }
    private double[] lerp( double[]p1,double[]p2,double t){
        return new double[]{ ((1-t)*p1[0]) + (t*p2[0]),
                ((1-t)*p1[1]) + (t*p2[1])};
    }
}