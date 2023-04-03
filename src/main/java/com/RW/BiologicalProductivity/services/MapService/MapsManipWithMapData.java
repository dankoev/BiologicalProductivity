package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;

import java.io.IOException;

/**
 * Constructor with MapData classes */
public class MapsManipWithMapData implements MapsManipulation {
    
    private final static double noDataValue = -99999.0;
    private final static double r = 2;

    private final MapData mapH;
    private final MapData mapCFT;
    private final MapData mapZ;
    private final MapData mapT;
    private final MapData mapN;
    
    
    public MapsManipWithMapData(MapData mapH, MapData mapCFT, MapData mapZ, MapData mapT, MapData mapN) {
        this.mapH = mapH;
        this.mapCFT = mapCFT;
        this.mapZ = mapZ;
        this.mapT = mapT;
        this.mapN = mapN;
    }
    
    @Override
    public MapSector  getSectorById(int sectorId, TypeMap typeMap) throws IOException {
        return switch (typeMap){
            case H -> mapH.getFillSector(sectorId);
            case CFT -> mapCFT.getFillSector(sectorId);
            case N -> mapN.getFillSector(sectorId);
            case T -> mapT.getFillSector(sectorId);
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
        return  MapsManipWithMapInfo.calculateFormula(firstSector,secondSector,numberFormula);
    }
    
    private double calcMapData(double firstData, double secondData, int numberFormula){
        return MapsManipWithMapInfo.calcMapData(firstData,secondData,numberFormula);
    }
}
