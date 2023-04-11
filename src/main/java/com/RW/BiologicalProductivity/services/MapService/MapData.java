package com.RW.BiologicalProductivity.services.MapService;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;


public class MapData {
    private static final double noDataValue = -99999.0;
    public static final  int typeMapCodding = Imgcodecs.IMREAD_UNCHANGED |
                                             Imgcodecs.IMREAD_ANYDEPTH |
                                             Imgcodecs.IMREAD_LOAD_GDAL;
    
    private final Mat img;
    private final String name;
    private int  imgCols = 0;
    private int imgRows = 0;

    public double maxMapValue = 0;
    public double minMapValue = 0;
    private final GdalService gdalSer;
    
    private int rowSplit = 8;
    private int colSplit = 8;
    public List<MapSector> sectors = new ArrayList<>();
    
    private MapData(MapInfo mapInfo) throws IOException {
        String path = mapInfo.getRegion().getMapsRootPath()+"/" + mapInfo.getName();
        this.gdalSer= new GdalService(path);
        Instant start = Instant.now();
        this.img = Imgcodecs.imread(path, typeMapCodding);
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Время загрузки изобрежения " + elapsed);
        this.imgCols = img.cols();
        this.imgRows = img.rows();
        this.name = mapInfo.getName();
        
        this.maxMapValue = mapInfo.getMaxValue();
        this.minMapValue = mapInfo.getMinValue();
    }
    public MapData(MapInfo mapInfo,
                   int rowSplit,
                   int colSplit) throws IOException {
        this(mapInfo);
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
    public synchronized MapSector getFillSector(int idSector){
        MapSector sector = sectors.get(idSector).clone();
        Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                sector.cols,sector.rows);
        sector.data = img.submat(rect).clone();
        return sector;
    }
    private double[] pixelToWord(int row, int col){
        return gdalSer.getGeoCoordByPixels(row,col);
    }
}