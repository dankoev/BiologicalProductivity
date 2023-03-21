package com.RW.BiologicalProductivity.services;
import com.RW.BiologicalProductivity.services.enums.TypeMap;
import com.RW.BiologicalProductivity.services.interfaces.MapsManipulation;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MapsManipulationImpl implements MapsManipulation {

    private final static double noDataValue = -99999.0;
    private final static double r = 2;

    public String pathMapH;
    public String pathMapCFT;
    public String pathMapN;
    public String pathMapT;
    private int rowSplit;
    private int colSplit;


    
    public MapsManipulationImpl(String pathMapH, String pathMapCFT, String pathMapN, String pathMapT, int rowSplit, int colSplit) {
        this.pathMapH = pathMapH;
        this.pathMapCFT = pathMapCFT;
        this.pathMapN = pathMapN;
        this.pathMapT = pathMapT;
        this.rowSplit = rowSplit;
        this.colSplit = colSplit;
    }
    @Override
    public MapSector  getSectorById(int sectorId, TypeMap typeMap) throws IOException {
        return switch (typeMap){
            case H -> {
                MapData mapH = new MapData(pathMapH,rowSplit,colSplit);
                yield mapH.getFillSector(sectorId);
            }
            case CFT -> {
                MapData mapCFT = new MapData(pathMapCFT,rowSplit,colSplit);
                yield mapCFT.getFillSector(sectorId);
            }
            case N -> {
                MapData mapN = new MapData(pathMapN,rowSplit,colSplit);
                yield mapN.getFillSector(sectorId);
            }
            case T -> {
                MapData mapT = new MapData(pathMapT,rowSplit,colSplit);
                yield mapT.getFillSector(sectorId);
            }
            case ZM -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorZM = calculateFormula(sectorT,sectorCFT,1);
                sectorZM.setMaxMinMapValue(820,71);
                yield sectorZM;
            }
            case BetaH -> {
                MapSector sectorZM = getSectorById(sectorId,TypeMap.ZM);
                MapSector sectorH = getSectorById(sectorId,TypeMap.H);
                MapSector sectorBetaH = calculateFormula(sectorH,sectorZM,2);
                sectorBetaH.setMaxMinMapValue(8,0);
                yield sectorBetaH;
            }
            case SY -> {
                MapSector sectorBetaH = getSectorById(sectorId,TypeMap.BetaH);
                MapSector sectorN = getSectorById(sectorId,TypeMap.N);
                MapSector sectorSY = calculateFormula(sectorBetaH,sectorN,3);
                sectorSY.setMaxMinMapValue(120,22);
                yield sectorSY;
            }
            case BP -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorSY = getSectorById(sectorId,TypeMap.SY);
                MapSector sectorTemp = calculateFormula(sectorSY,sectorCFT,4);
                MapSector sectorBP = calculateFormula(sectorTemp,sectorT,5);
                sectorBP.setMaxMinMapValue(10,0);
                yield sectorBP;
            }
        };
    }
    private MapSector calculateFormula(MapSector firstSector,
                                       MapSector secondSector,
                                       int numberFormula){
            MapSector newSector = firstSector.clone();
            newSector.mapDataList.clear();
            MapElementData mapData;
            for (int i = 0 ; i < firstSector.mapDataList.size(); i++) {
                mapData = calcMapData(firstSector.mapDataList.get(i),
                        secondSector.mapDataList.get(i),numberFormula);
                newSector.mapDataList.add(mapData);
            }
            return newSector;
    }
    
    private MapElementData calcMapData(MapElementData firstData, MapElementData secondData, int numberFormula){
        MapElementData newMapData = new MapElementData();
        double newValue;
        if (firstData.isNoData || secondData.isNoData) {
            newMapData.setData(noDataValue, firstData.x, firstData.y);
            return newMapData;
        }
        switch (numberFormula) {
            case 1 -> newValue = (200 * firstData.value / 1000 + 306) * secondData.value;
            case 2 -> newValue = firstData.value / secondData.value;
            case 3 -> {
                int r = 2;
                double multiplier = (r - 1) / (r * secondData.value + 1);
                double pow = 1 / (r * secondData.value);
                newValue = 100 * firstData.value * Math.pow(multiplier, pow);
            }
            case 4 ->//fourthPart1
                    newValue = 0.0045 * (1 - Math.abs(64 - firstData.value) / 64) * secondData.value;
            case 5 ->//fourthPart2
                    newValue = firstData.value * secondData.value - 1;
            default -> newValue = noDataValue;
        }
        
        newMapData.setData(newValue, firstData.x, firstData.y);
        return newMapData;
    }

}


