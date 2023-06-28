package com.RW.BiologicalProductivity.services.MapService;
import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapsManipulation;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Constructor with MapInfo classes */
public class MapsManipWithMapInfo implements MapsManipulation {

    private final static double noDataValue = -99999.0;
    private final static double r = 2;

    private final MapInfo InfoMapH;
    private final MapInfo InfoMapCFT;
    private final MapInfo InfoMapN;
    private final MapInfo InfoMapT;
    private final int rowSplit;
    private final int colSplit;


    
    public MapsManipWithMapInfo(List<MapInfo> mapsInfo, int rowSplit, int colSplit) throws DataBaseException {
        try {
            this.InfoMapH = getMapInfo(TypeMap.H,mapsInfo);
            this.InfoMapCFT = getMapInfo(TypeMap.CFT,mapsInfo);
            this.InfoMapN = getMapInfo(TypeMap.N,mapsInfo);
            this.InfoMapT = getMapInfo(TypeMap.T,mapsInfo);
        }catch (NoSuchValueException e){
            throw new DataBaseException("Не удалось расчитать карты");
        }
        this.rowSplit = rowSplit;
        this.colSplit = colSplit;
    }
    public static MapInfo getMapInfo(TypeMap typeMap, List<MapInfo> mapsInfo) throws NoSuchValueException {
        return mapsInfo.stream()
                .filter(info -> info.getType() == typeMap)
                .findFirst()
                .orElseThrow(() -> new NoSuchValueException("Нет карты: " + typeMap.name()));
    }
    @Override
    public MapSector  getSectorById(int sectorId, TypeMap typeMap) throws IOException {
        return switch (typeMap){
            case H -> {
                MapDataGrid mapH = new MapDataGrid(InfoMapH,rowSplit,colSplit);
                yield mapH.getFillSector(sectorId);
            }
            case CFT -> {
                MapDataGrid mapCFT = new MapDataGrid(InfoMapCFT,rowSplit,colSplit);
                yield mapCFT.getFillSector(sectorId);
            }
            case N -> {
                MapDataGrid mapN = new MapDataGrid(InfoMapN,rowSplit,colSplit);
                yield mapN.getFillSector(sectorId);
            }
            case T -> {
                MapDataGrid mapT = new MapDataGrid(InfoMapT,rowSplit,colSplit);
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
    public  static  MapSector calculateFormula(MapSector firstSector,
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
        double newValue;
        for (int i = 0; i < firstSector.data.rows(); i++) {
            for (int j = 0; j < firstSector.data.cols() ; j++){
                newValue = calcMapData(firstSector.data.get(i,j)[0],
                        secondSector.data.get(i,j)[0],numberFormula);
                newSector.data.put(i,j,newValue);
            }
        }
        end  = Instant.now();
        System.out.println("Время расчета формулы " + numberFormula
                + ": " + Duration.between(start,end).toMillis());
        return newSector;
    }
    
    public static double  calcMapData(double firstData, double secondData, int numberFormula){
        if (firstData == noDataValue || secondData == noDataValue){
            return noDataValue;
        }
        switch (numberFormula) {
            case 1 -> {
                return (200 * firstData / 1000 + 306) * secondData;
            }
            case 2 -> {
                return firstData / secondData;
            }
            case 3 -> {
                int r = 2;
                double multiplier = (r - 1) / (r * secondData + 1);
                double pow = 1 / (r * secondData);
                return 100 * firstData * Math.pow(multiplier, pow);
            }
            case 4 ->{
                //fourthPart1
                return  0.0045 * (1 - Math.abs(64 - firstData) / 64) * secondData;
            }
            case 5 ->{
                //fourthPart2
                return firstData * secondData - 1;
            }
            default -> {
                return noDataValue;
            }
        }
    }

}


