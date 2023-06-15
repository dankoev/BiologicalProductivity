package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public class MapsManipParall extends MapsManipWithMapData{
    
    
    public MapsManipParall(List<MapInfo> mapsInfo, int rowSplit, int colSplit) throws IOException, DataBaseException {
        super(mapsInfo, rowSplit, colSplit);
    }
    
    
    @Override
    public   MapSector calculateFormula(MapSector firstSector,
                                               MapSector secondSector,
                                               int numberFormula){
        Instant start = Instant.now();
        Instant end;
        MapSector newSector = firstSector.clone();
        if (newSector.hasNoData){
            end = Instant.now();
            System.out.println("Время расчета формулы " + numberFormula
                    + ": " + Duration.between(start,end).toMillis());
            return newSector;
        }
        int rows = newSector.rows;
        int cols = newSector.cols;
        double[][] result = IntStream.range(0, rows)
                .parallel()
                .mapToObj(i -> IntStream.range(0, cols)
                        .mapToDouble(j -> calcMapData(firstSector.data.get(i,j)[0],
                                secondSector.data.get(i,j)[0],numberFormula))
                        .toArray())
                .toArray(double[][]::new);
    
        for (int i = 0; i < rows; i++) {
            newSector.data.put(i, 0, result[i]);
        }
        
        
        end  = Instant.now();
        System.out.println("Время расчета формулы " + numberFormula
                + ": " + Duration.between(start,end).toMillis());
        return newSector;
    }
    
}
