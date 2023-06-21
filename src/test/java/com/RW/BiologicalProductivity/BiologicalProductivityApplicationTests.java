package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.exceptions.NoSuchValueException;
import com.RW.BiologicalProductivity.services.DB.exceptions.OverwriteAttemptException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapDeploymentImpl;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapDeploymentParall;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapUploadService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapDeployment;

import com.RW.BiologicalProductivity.services.MapService.MapApiImpl;
import com.RW.BiologicalProductivity.services.MapService.MapData;
import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;

import com.RW.BiologicalProductivity.services.MapService.interfaces.MapAPI;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BiologicalProductivityApplicationTests {
	static {
		nu.pattern.OpenCV.loadLocally();
		System.out.println("Load GDAL library");
	}
	@Autowired
	RegionService regionService;
	@Autowired
	MapInfoService mapInfoService;
//
	@Test
	void moduleTest()  {
		try{
			MapUploadService m = new MapUploadService(regionService,mapInfoService);
			m.checkMapsDirectory();
		}catch ( IOException  | DataBaseException e){
			System.err.println(e.getMessage());
		}
	}
	@Test
	void test1()  {
		try{
			MapUploadService m = new MapUploadService(regionService,mapInfoService);
			m.checkMapsDirectory();
			List<TypeMap> typeMapList = new ArrayList<>();
			typeMapList.add(TypeMap.ZM);
			typeMapList.add(TypeMap.BetaH);
			MapDeployment mapDeployment = new MapDeploymentImpl(regionService,mapInfoService,"region_1");
			for (TypeMap typeMap : typeMapList) {
				try {
					mapDeployment.deployMap(typeMap);
				}catch (OverwriteAttemptException e){
					System.err.println(e.getMessage());
				}
			}
		}catch ( IOException  | DataBaseException e){
			System.err.println(e.getMessage());
		}catch (InterruptedException e){
			System.err.println("Program error: Error parallel deploy");
		}


	}
//	@Test
//	void test3() throws IOException, DataBaseException {
//		Instant start = Instant.now();
//
//		MapDeploymentImpl mapDeploymentImpl = new MapDeploymentImpl(regionService,mapInfoService,"region_1");
//		mapDeploymentImpl.deployMap(TypeMap.ZM);
//
//		Instant finish = Instant.now();
//		System.out.println("Oбщее время выполнения "
//				+ Duration.between(start,finish).toMillis());
////		Imgcodecs.imwrite("./sectors/mfd.jpeg",im1);
//	}
//
	@Test
	void test5() throws IOException, InterruptedException, DataBaseException {
		Instant start = Instant.now();

		MapDeployment mapDeployment = new MapDeploymentParall(regionService,mapInfoService,"region_1");
		mapDeployment.setColSplit(14);
		mapDeployment.setRowSplit(14);
		mapDeployment.deployMap(TypeMap.BP);
		
		Instant finish = Instant.now();
		System.out.println("Oбщее время выполнения "
				+ Duration.between(start,finish).toMillis());
	}

	@Test
	void test2() throws IOException, InterruptedException, DataBaseException {
		Instant start = Instant.now();
		
		MapApiImpl api = new MapApiImpl(regionService);
		api.detectRegion(new double[][]{
				{100.3918,56.3898},
				{104.1271,54.9368}});
		api.getSectorAsBytes(new double[][]{
				{100.3918,56.3898},
				{104.1271,54.9368}},TypeMap.BP);
		
		
		Instant finish = Instant.now();
		long elapsed = Duration.between(start, finish).toMillis();
		System.out.println("Прошло времени, мс: " + elapsed);
	}
	@Test
	void test() throws IOException, InterruptedException, DataBaseException {
		Instant start = Instant.now();
		
		MapData mapData = new MapData(regionService.getMapInfo("region_1",TypeMap.BP));
		Instant finish = Instant.now();
		long elapsed = Duration.between(start, finish).toMillis();
		System.out.println("Прошло времени, мс: " + elapsed);
		
		Mat sector = mapData
				.getFillSector(new double[][]{
						{100.3918,56.3898},
						{104.1271,54.9368}})
				.data;
		
	
	}

	
}
