package com.RW.BiologicalProductivity.services.MapService;

import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MapApiImpl implements MapAPI {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
    }
    private  Region region;
    private final RegionService regionService;
    
    public MapApiImpl(RegionService regionService) {
        this.regionService = regionService;
    }
    
    public void detectRegion(double[] latLongs) throws NoSuchValueException {
        this.region  = regionService.getInfo("region_1");
    }
    public byte[] getSectorAsBytes(double[][] twoPoints, TypeMap typeMap) throws IOException, NoSuchValueException {
        String region_name = region.getName();
        MapData mapData = new MapData(regionService.getMapInfo(region_name,typeMap));
        Mat sector = mapData
                .getFillSector(twoPoints)
                .toHeatMap();
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png",sector,buf);
        return buf.toArray();
    }
    public byte[] getSectorAsBytes(double[][] twoPoints, double[][] areaWordCoords, TypeMap typeMap) throws IOException, NoSuchValueException {
        String region_name = region.getName();
        MapData mapData = new MapData(regionService.getMapInfo(region_name,typeMap));
        MapSector sector = mapData
                .getFillSector(twoPoints);
        Mat mask  = new Mat(sector.rows, sector.cols, CvType.CV_8UC1,new Scalar(1));
        ArrayList<Point> localPoints = new ArrayList<>();
        
        for (double[] point: areaWordCoords) {
            Point convertedPoint = mapData.gdalSer.getPointByWords(point[0],point[1]);
            localPoints.add(new Point(convertedPoint.x - sector.offsetCols ,
                    convertedPoint.y - sector.offsetRows ));
        }
        MatOfPoint points = new MatOfPoint();
        points.fromList(localPoints);
        ArrayList<MatOfPoint> result = new ArrayList<>(1);
        result.add(points);
        
        Imgproc.fillPoly(mask,result,new Scalar(0));
        sector.data.setTo(new Scalar(MapSector.noDataValue), mask);
        
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png",sector.toHeatMap(),buf);
        return buf.toArray();
    }

}
