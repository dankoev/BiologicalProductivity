package com.RW.BiologicalProductivity.services;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ServicesAPI extends Calculate {

    private static MapSector calculatedSector;
    public static Mat getHeatMap(int idSectors, int typeMap ){
       return getSector(idSectors, typeMap).toRGB();
    }
    public static String getPathToHeatMap(int idSectors, int typeMap ){
        Mat heatMap = getHeatMap(idSectors, typeMap);
        String pathToFolder = "./uploads/heatMaps/";
        String pathToHeatMap = pathToFolder + "sector.jpeg";
        Imgcodecs.imwrite(pathToHeatMap, heatMap);
        System.out.println("******Файл Загружен*****");
        return pathToHeatMap;
     }
    
    private static MapSector getSector(int idSectors, int typeMap ) {
        readMapInfo();
        createMapsData();
        MapsManipulation manipulation = new MapsManipulation(mapHData, mapCFTData, mapNData, mapT10Data);
        if (typeMap > 4){
            calculatedSector = manipulation.calculate(idSectors, typeMap - 4);
        }
        else{
            calculatedSector = manipulation.getSectorById(idSectors, typeMap);
        }
        return calculatedSector;
    }
    
}
