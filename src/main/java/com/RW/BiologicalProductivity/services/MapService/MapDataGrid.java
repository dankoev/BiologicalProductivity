package com.RW.BiologicalProductivity.services.MapService;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;


public class MapDataGrid extends MapData {
    
    private int rowSplit = 8;
    private int colSplit = 8;
    private final List<MapSector> sectors = new ArrayList<>();
    
    public MapDataGrid(MapInfo mapInfo,
                       int rowSplit,
                       int colSplit) throws IOException {
        super(mapInfo);
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
        countSectors();
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
                offsetRows = i < rowRemainder ? (RowSize+1) * i: RowSize * i + rowRemainder;
                rows = i < rowRemainder ? RowSize+1: RowSize;
                
                offsetCols = j < colRemainder ? (ColSize+1) * j: ColSize * j + colRemainder;
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
    public synchronized MapSector getFillSector(int idSector){
        MapSector sector = sectors.get(idSector).clone();
        Rect rect = new Rect(sector.offsetCols,sector.offsetRows,
                sector.cols,sector.rows);
        sector.data = img.submat(rect);
        return sector;
    }
}