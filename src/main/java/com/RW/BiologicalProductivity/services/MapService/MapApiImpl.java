package com.RW.BiologicalProductivity.services.MapService;
import com.RW.BiologicalProductivity.services.DB.entities.Region;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class MapApiImpl implements MapAPI {
    private  Region region;
    private final RegionService regionService;
    private MapSector currentSector = null;
    public MapApiImpl(RegionService regionService) {
        this.regionService = regionService;
    }
    
    public void detectRegion(double[][] twoPoints) throws NoSuchValueException {
        List<Region>  regions = regionService.getRegionsInfo();
        Point firstPointArea = new Point(twoPoints[0][0],twoPoints[0][1]);
        Point secondPointArea = new Point(twoPoints[1][0],twoPoints[1][1]);
        for (Region region: regions) {
            boolean areaInsideRegion = pointInsideRegion(firstPointArea,region)
                    && pointInsideRegion(secondPointArea, region);
            if (areaInsideRegion){
                this.region = region;
                return;
            }
        }
        throw new NoSuchValueException("Map API: The boundaries of the selected area go beyond the areas in which there is information");
        
    }
    private boolean pointInsideRegion(Point point, Region region){
        return !(point.x > region.getRightLong() || point.x < region.getLeftLong()
                || point.y > region.getTopLat() || point.y < region.getBottomLat());
    }
    public byte[] getSectorAsBytes(double[][] twoPoints, TypeMap typeMap) throws IOException, NoSuchValueException {
        String region_name;
        try {
            region_name = region.getName();
        } catch ( NullPointerException e){
            throw new NoSuchObjectException("Map API: region not detected ");
        }
        MapData mapData = new MapData(regionService.getMapInfo(region_name,typeMap));
        MapSector sector = mapData
                .getFillSector(twoPoints);
        new Thread(() ->{
            sector.calculateOptionalInfo();
            currentSector = sector;
        }).start();
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png",sector.toHeatMap(),buf);
        return buf.toArray();
    }
    public byte[] getSectorAsBytes(double[][] twoPoints, double[][] areaWordCoords, TypeMap typeMap) throws IOException, NoSuchValueException {
        Instant start = Instant.now();
        
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
        
        new Thread(() ->{
            sector.calculateOptionalInfo();
            currentSector = sector;
        }).start();
    
        Instant finish = Instant.now();
        System.out.println("Время Заполенения "
                + Duration.between(start,finish).toMillis());
        
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png",sector.toHeatMap(),buf);
        return buf.toArray();
    }
    
    public MapSector getCurrentSector() {
        return currentSector;
    }
}
