package com.RW.BiologicalProductivity.services;
import java.util.ArrayList;
import java.util.List;

public class MapsManipulation {

    private final static double noDataValue = -99999.0;

    public MapData mapH;
    public MapData mapCFT;
    public MapData mapN;
    public MapData mapT;

    private MapSector sectorH;
    private MapSector sectorCFT;
    private MapSector sectorN;
    private MapSector sectorT;
    private final List<MapSector> initialSectors = new ArrayList<>();

    public MapsManipulation(MapData mapH, MapData mapCFT, MapData mapN, MapData mapT) {//for 4'th Sectors
        this.mapH = mapH;
        this.mapCFT = mapCFT;
        this.mapN = mapN;
        this.mapT = mapT;
    }
    private void  getSectorsById(int sectorId){
        initialSectors.clear();
        sectorH = this.mapH.getFillSector(sectorId);
        sectorCFT = this.mapCFT.getFillSector(sectorId);
        sectorN = this.mapN.getFillSector(sectorId);
        sectorT = this.mapT.getFillSector(sectorId);
        initialSectors.add(sectorH);
        initialSectors.add(sectorCFT);
        initialSectors.add(sectorN);
        initialSectors.add(sectorT);
    }
    public MapSector  getSectorById(int sectorId,int typeMap ){
        return switch (typeMap){
            case 1 -> {
                sectorH = mapH.getFillSector(sectorId);
                yield sectorH;
            }
            case 2 -> {
                sectorCFT = mapCFT.getFillSector(sectorId);
                yield sectorCFT;
            }
            case 3 -> {
                sectorN = mapN.getFillSector(sectorId);
                yield sectorN;
            }
            case 4 -> {
                sectorT = mapT.getFillSector(sectorId);
                yield sectorT;
            }
            default -> null;

        };

    }
    public MapSector calculate(int sectorsId, int typeMap){
        MapSector tempSector,sectorZm,sectorBetaH,sectorSY,sectorBP;
        getSectorsById(sectorsId);
        for (MapSector sector : initialSectors) {
            if (sector.hasNoData) {
                System.out.println("No Data in sectors");
                return sector;
            }
        }
        return switch (typeMap){
            case 1 -> {
                sectorZm = calculateFormula(sectorT, sectorCFT,1);
                sectorZm.setMaxMinMapValue(820,71);
                yield sectorZm;
            }
            case 2 -> {
                sectorZm = calculateFormula(sectorT, sectorCFT, 1);
                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);
                sectorBetaH.setMaxMinMapValue(8,0);
                yield sectorBetaH;
            }
            case 3 -> {
                sectorZm = calculateFormula(sectorT, sectorCFT, 1);
                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);
                sectorSY = calculateFormula(sectorBetaH, sectorN, 3);
                sectorSY.setMaxMinMapValue(120,22);
                yield sectorSY;
            }
            case 4 -> {
                sectorZm = calculateFormula(sectorT, sectorCFT, 1);//mapZm //teEvaporationResources
                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);//betaH // climaticHumidity
                sectorSY = calculateFormula(sectorBetaH, sectorN, 3);//SY // humidificationStage
                tempSector = calculateFormula(sectorSY, sectorCFT, 4);//
                sectorBP = calculateFormula(tempSector, sectorT, 5);//biologicalProductivity
                sectorBP.setMaxMinMapValue(10,0);
                yield sectorBP;
            }
            default -> throw new NumberFormatException("Номер расчётной формулы больше 4 или не int");
        };
    }
    private MapSector calculateFormula(MapSector firstSector, MapSector secondSector, int numberFormula){
        try {
            MapSector newSector = firstSector.clone();
            newSector.mapDataList.clear();
            MapElementData mapData;
            for (int i = 0 ; i < firstSector.mapDataList.size(); i++) {

                mapData = calcMapData(firstSector.mapDataList.get(i),
                        secondSector.mapDataList.get(i),numberFormula);
                newSector.mapDataList.add(mapData);
            }
            return newSector;

        } catch (CloneNotSupportedException e) {
            System.out.println("Ошибка при клонировании объекта в calculateFormula");
            return null;
        }
    }
    private MapElementData calcMapData(MapElementData firstData, MapElementData secondData, int numberFormula){
        MapElementData newMapData = new MapElementData();
        double newValue;
        if (!firstData.isNoData & !secondData.isNoData){
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
        }
        else {
            newMapData.setData(noDataValue, firstData.x, firstData.y);
        }
        return newMapData;
    }

}
