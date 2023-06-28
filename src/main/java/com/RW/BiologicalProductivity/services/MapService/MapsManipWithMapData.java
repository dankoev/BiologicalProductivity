package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;

import java.io.IOException;
import java.util.List;

/**
 * Constructor with MapData classes */
public class MapsManipWithMapData implements MapsManipulation {
    
    private final static double noDataValue = -99999.0;
    private final static double r = 2;

    private final MapDataGrid mapH;
    private final MapDataGrid mapCFT;
    private final MapDataGrid mapT;
    private final MapDataGrid mapN;
    

    public MapsManipWithMapData(List<MapInfo> mapsInfo, int rowSplit, int colSplit) throws IOException, DataBaseException {
        try {
            this.mapH = new MapDataGrid(getMapInfo(TypeMap.H,mapsInfo),rowSplit,colSplit);
            this.mapCFT = new MapDataGrid(getMapInfo(TypeMap.CFT,mapsInfo),rowSplit,colSplit);
            this.mapN = new MapDataGrid(getMapInfo(TypeMap.N,mapsInfo),rowSplit,colSplit);
            this.mapT = new MapDataGrid(getMapInfo(TypeMap.T,mapsInfo),rowSplit,colSplit);
        }catch (NoSuchValueException e){
            throw new DataBaseException("Error calculate maps. Check the presence of all initial maps in the database");
        }
    }
    private static MapInfo getMapInfo(TypeMap typeMap, List<MapInfo> mapsInfo) throws NoSuchValueException {
        return MapsManipWithMapInfo.getMapInfo(typeMap,mapsInfo);
    }
    
    @Override
    public MapSector  getSectorById(int sectorId, TypeMap typeMap) {
        return switch (typeMap){
            case H -> mapH.getFillSector(sectorId);
            case CFT -> mapCFT.getFillSector(sectorId);
            case N -> mapN.getFillSector(sectorId);
            case T -> mapT.getFillSector(sectorId);
            case ZM -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorZM = calculateFormula(sectorT,sectorCFT,1);
//                sectorZM.setMaxMinMapValue(820,71);
                yield sectorZM;
            }
            case BetaH -> {
                MapSector sectorZM = getSectorById(sectorId,TypeMap.ZM);
                MapSector sectorH = getSectorById(sectorId,TypeMap.H);
                MapSector sectorBetaH = calculateFormula(sectorH,sectorZM,2);
//                sectorBetaH.setMaxMinMapValue(8,0);
                yield sectorBetaH;
            }
            case SY -> {
                MapSector sectorBetaH = getSectorById(sectorId,TypeMap.BetaH);
                MapSector sectorN = getSectorById(sectorId,TypeMap.N);
                MapSector sectorSY = calculateFormula(sectorBetaH,sectorN,3);
//                sectorSY.setMaxMinMapValue(120,22);
                yield sectorSY;
            }
            case BP -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorSY = getSectorById(sectorId,TypeMap.SY);
                MapSector sectorTemp = calculateFormula(sectorSY,sectorCFT,4);
                MapSector sectorBP = calculateFormula(sectorTemp,sectorT,5);
//                sectorBP.setMaxMinMapValue(10,0);
                yield sectorBP;
            }
        };
    }
    protected MapSector calculateFormula(MapSector firstSector,
                                       MapSector secondSector,
                                       int numberFormula){
        return  MapsManipWithMapInfo.calculateFormula(firstSector,secondSector,numberFormula);
    }
    
    protected double calcMapData(double firstData, double secondData, int numberFormula){
        return MapsManipWithMapInfo.calcMapData(firstData,secondData,numberFormula);
    }
}
