package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MapData {
    private static final double noDataValue = -99999.0;
    public static final  int typeMapCodding = Imgcodecs.IMREAD_LOAD_GDAL;
    
    protected final Mat img;
    protected final String name;
    protected int  imgCols;
    protected int imgRows;
    
    protected double maxMapValue;
    protected double minMapValue;
    protected final GdalService gdalSer;
    
    public MapData(MapInfo mapInfo) throws IOException {
        String path = mapInfo.getRegion().getMapsRootPath()+"/" + mapInfo.getName();
        this.gdalSer= new GdalService(path);
        
        Instant start = Instant.now();
        this.img = Imgcodecs.imread(path, typeMapCodding);
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Время загрузки изображения " + elapsed);
        
        this.imgCols = img.cols();
        this.imgRows = img.rows();
        this.name = mapInfo.getName();
        
        this.maxMapValue = gdalSer.getMinMax(1)[1];
        this.minMapValue = gdalSer.getMinMax(1)[0];
    }
    protected double[][] calcCornWordsCoord(int offsetRows, int offsetCols, int rows, int cols){
        double[][] cornerCoords = new double[4][2];
        cornerCoords[0] = pixelToWord(offsetRows,offsetCols);
        cornerCoords[1] = pixelToWord(offsetRows,offsetCols + cols);
        cornerCoords[2] = pixelToWord(offsetRows + rows,offsetCols + cols);
        cornerCoords[3] = pixelToWord(offsetRows  + rows,offsetCols);
        return cornerCoords;
        
    }
    protected boolean detectNoDataSectors(int offsetRows, int offsetCols, int rows, int cols){
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
    
    /**
     * twoPoints - [[long,lat],[long,lat]]
     * @param twoPoints
     * @return
     */
    public MapSector getFillSector(double[][] twoPoints){
        Rect rect = gdalSer.getRectByLatsLongs(twoPoints);
        MapSector sector = new MapSector();
        sector.setMaxMinMapValue(maxMapValue,minMapValue);
        double [][] cornWordsCoord = new double[][]{
                twoPoints[0],
                twoPoints[1]
        };
//        boolean hasNoData = detectNoDataSectors(rect.y, rect.x, rect.height, rect.width);
        sector.setInitialData(0,rect.y, rect.x, rect.height, rect.width,cornWordsCoord,false);
        Mat sectorFromMap = img.submat(rect);
        sectorFromMap.copyTo(sector.data);
        return sector;
    }
    protected double[] pixelToWord(int row, int col){
        return gdalSer.getGeoCoordByPixels(row,col);
    }
    protected void clear (){
        this.img.release();
    }
}
