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

    private MapSector sectorH;
    private MapSector sectorCFT;
    private MapSector sectorN;
    private MapSector sectorT;
    
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
                sectorH = mapH.getFillSector(sectorId);
                yield sectorH;
            }
            case CFT -> {
                MapData mapCFT = new MapData(pathMapCFT,rowSplit,colSplit);
                sectorCFT = mapCFT.getFillSector(sectorId);
                yield sectorCFT;
            }
            case N -> {
                MapData mapN = new MapData(pathMapN,rowSplit,colSplit);
                sectorN = mapN.getFillSector(sectorId);
                yield sectorN;
            }
            case T -> {
                MapData mapT = new MapData(pathMapT,rowSplit,colSplit);
                sectorT = mapT.getFillSector(sectorId);
                yield sectorT;
            }
            case ZM -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorZM = calculateFormula(sectorT,sectorCFT,1);
                if (sectorZM != null)
                    sectorZM.setMaxMinMapValue(820,71);
                yield sectorZM;
            }
            case BetaH -> {
                MapSector sectorZM = getSectorById(sectorId,TypeMap.ZM);
                MapSector sectorH = getSectorById(sectorId,TypeMap.H);
                MapSector sectorBetaH = calculateFormula(sectorH,sectorZM,2);
                if (sectorBetaH != null)
                    sectorBetaH.setMaxMinMapValue(8,0);
                yield sectorBetaH;
            }
            case SY -> {
                MapSector sectorBetaH = getSectorById(sectorId,TypeMap.BetaH);
                MapSector sectorN = getSectorById(sectorId,TypeMap.N);
                MapSector sectorSY = calculateFormula(sectorBetaH,sectorN,3);
                if (sectorSY != null)
                    sectorSY.setMaxMinMapValue(120,22);
                yield sectorSY;
            }
            case BP -> {
                MapSector sectorT = getSectorById(sectorId,TypeMap.T);
                MapSector sectorCFT = getSectorById(sectorId,TypeMap.CFT);
                MapSector sectorSY = getSectorById(sectorId,TypeMap.SY);
                MapSector sectorTemp = calculateFormula(sectorSY,sectorCFT,4);
                MapSector sectorBP = calculateFormula(sectorTemp,sectorT,5);
                yield sectorBP;
            }
            default -> null;

        };

    }
    
//    public MapSector calculate(int sectorsId, int typeMap){
//        MapSector tempSector,sectorZm,sectorBetaH,sectorSY,sectorBP;
//        getSectorsById(sectorsId);
//        for (MapSector sector : initialSectors) {
//            if (sector.hasNoData) {
//                System.out.println("No Data in sectors");
//                return sector;
//            }
//        }
//        return switch (typeMap){
//            case 1 -> {
//                sectorZm = calculateFormula(sectorT, sectorCFT,1);
//                sectorZm.setMaxMinMapValue(820,71);
//                yield sectorZm;
//            }
//            case 2 -> {
//                sectorZm = calculateFormula(sectorT, sectorCFT, 1);
//                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);
//                sectorBetaH.setMaxMinMapValue(8,0);
//                yield sectorBetaH;
//            }
//            case 3 -> {
//                sectorZm = calculateFormula(sectorT, sectorCFT, 1);
//                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);
//                sectorSY = calculateFormula(sectorBetaH, sectorN, 3);
//                sectorSY.setMaxMinMapValue(120,22);
//                yield sectorSY;
//            }
//            case 4 -> {
//                sectorZm = calculateFormula(sectorT, sectorCFT, 1);//mapZm //teEvaporationResources
//                sectorBetaH = calculateFormula(sectorH, sectorZm, 2);//betaH // climaticHumidity
//                sectorSY = calculateFormula(sectorBetaH, sectorN, 3);//SY // humidificationStage
//                tempSector = calculateFormula(sectorSY, sectorCFT, 4);//
//                sectorBP = calculateFormula(tempSector, sectorT, 5);//biologicalProductivity
//                sectorBP.setMaxMinMapValue(10,0);
//                yield sectorBP;
//            }
//            default -> throw new NumberFormatException("Номер расчётной формулы больше 4 или не int");
//        };
//    }
    private MapSector calculateFormula(MapSector firstSector, MapSector secondSector, int numberFormula){
        MapSector calculatedSector = new MapSector(firstSector);
        Mat mat = new Mat();
        Mat firstMat = firstSector.sertorData;
        Mat secondMat = secondSector.sertorData;
        double value;
        if (firstSector.hasNoData || secondSector.hasNoData)
            return null;
        switch (numberFormula) {
                case 1 ->  {
                    mulToConst(firstMat,200.0/1000.0,firstMat);
                    addToConst(firstMat,306.0,firstMat);
                    mat = firstMat.mul(secondMat);
                }
                case 2 ->  divide(firstMat,secondMat,mat);
                case 3 -> {
                    Mat multiplier = new Mat();
                    Mat power = new Mat();
                    
                    mulToConst(secondMat,r,multiplier);
                    addToConst(multiplier,1.0,multiplier);
                    pow(multiplier,-1.0,multiplier);
                    mulToConst(multiplier,(r-1),multiplier);
                    
                    mulToConst(secondMat,r,power);
                    pow(power,-1.0,power);
                    
                    mulToConst(firstMat,100,firstMat);
                    pow(multiplier,power,multiplier);
                    mat = firstMat.mul(multiplier);
                    
                }
//                case 4 ->//fourthPart1
//                        newValue = 0.0045 * (1 - Math.abs(64 - firstData.value) / 64) * secondData.value;
//                case 5 ->//fourthPart2
//                        newValue = firstData.value * secondData.value - 1;
                default -> mat = null;
            }
        calculatedSector.sertorData = mat;
        return calculatedSector;
    }
    private void divide(Mat src1,Mat src2,Mat dst){
        Core.divide(src1,src2,dst);
    }
    private void divide(Mat src1,int constant,Mat dst){
        Core.divide(src1,new Scalar(constant),dst);
    }
    private void pow(Mat src1,double constant,Mat dst){
        Core.pow(src1,constant,dst);
    }
    private void pow(Mat src1,Mat powers,Mat dst){
        for (int i = 0; i < src1.rows(); i++) {
            for (int j = 0; j < src1.cols(); j++) {
                double power = powers.get(i, j)[0];
                double value = src1.get(i, j)[0];
                double newValue = Math.pow(value, power);
                dst.put(i, j, newValue);
            }
        }
    }
    private void mulToConst(Mat mat,double constant,Mat dst){
        Core.multiply(mat,new Scalar(constant),dst);
    }
    private void addToConst(Mat mat,double constant,Mat dst){
        Core.add(mat,new Scalar(constant),dst);
    }
    

}


//    private MapSector calculateFormula(MapSector firstSector, MapSector secondSector, int numberFormula){
//        if (firstSector.hasNoData || secondSector.hasNoData)
//            return null;
//        if (!firstData.isNoData & !secondData.isNoData){
//            switch (numberFormula) {
//                case 1 -> newValue = (200 * firstData.value / 1000 + 306) * secondData.value;
//                case 2 -> newValue = firstData.value / secondData.value;
//                case 3 -> {
//                    int r = 2;
//                    double multiplier = (r - 1) / (r * secondData.value + 1);
//                    double pow = 1 / (r * secondData.value);
//                    newValue = 100 * firstData.value * Math.pow(multiplier, pow);
//                }
//                case 4 ->//fourthPart1
//                        newValue = 0.0045 * (1 - Math.abs(64 - firstData.value) / 64) * secondData.value;
//                case 5 ->//fourthPart2
//                        newValue = firstData.value * secondData.value - 1;
//                default -> newValue = noDataValue;
//            }
//            newMapData.setData(newValue, firstData.x, firstData.y);
//        }
//        else {
//            newMapData.setData(noDataValue, firstData.x, firstData.y);
//        }
//        return newMapData;
//    }