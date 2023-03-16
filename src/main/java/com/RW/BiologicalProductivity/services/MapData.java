package com.RW.BiologicalProductivity.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;


public class MapData {
    private static final double noDataValue = -99999.0;
    public static final  int typeMapCoding = Imgcodecs.IMREAD_UNCHANGED | 
                                             Imgcodecs.IMREAD_ANYDEPTH |
                                             Imgcodecs.IMREAD_LOAD_GDAL;
    
    private final Mat img;
    private int imgCols = 0;
    private int imgRows = 0;

    public double maxMapValue = 0;
    public double minMapValue = 0;
    private GdalService gdalSer;

    public double[] upLeftCoords;
    public double[] lowerLeftCoords;
    public double[] upRightCoords;
    public double[] lowerRightCoords;
    
    private int rowSplit = 8;
    private int colSplit = 8;
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
    public MapData(String pathToMap) throws IOException {
        this.img = Imgcodecs.imread(pathToMap, typeMapCoding);
        this.imgCols = img.cols();
        this.imgRows = img.rows();
        this.gdalSer= new GdalService(pathToMap);
        
        this.maxMapValue = gdalSer.getMinMax(1)[1];;
        this.minMapValue = gdalSer.getMinMax(1)[0];;
        countSectors();
    }
    public MapData(String pathToMap,
                   int rowSplit,
                   int colSplit) throws IOException {
        this(pathToMap);
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
        countSectors();
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
        
        int rowRemainder = imgRows % rowSplit;
        int colRemainder = imgCols % colSplit;
        int RowSize = imgRows/rowSplit;
        int ColSize = imgCols/colSplit;
        
        for( int i = 0; i < rowSplit; i++) {
            for (int j = 0; j < colSplit; j++) {
                MapSector sector = new MapSector();
                offsetRows = i < rowRemainder ? (RowSize+1) * i: RowSize * i;
                rows = i < rowRemainder ? RowSize+1: RowSize;
                
                offsetCols = j < colRemainder ? (ColSize+1) * j: ColSize * j;
                cols = j < colRemainder ? ColSize+1: ColSize;
                
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

        if (sector.hasNoData)//????????
            return sector;
        Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                sector.cols,sector.rows);
        sector.sertorData = img.submat(rect);
        return sector;
    }
    private double[] pixelToWord(int row, int col){
        return gdalSer.getGeoCoordByPixels(row,col);
    }
}