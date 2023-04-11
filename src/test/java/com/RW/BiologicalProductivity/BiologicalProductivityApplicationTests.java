package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.GDAL.GdalService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapDeploymentImpl;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapDeploymentParall;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapUploadService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.interfaces.MapDeployment;
import com.RW.BiologicalProductivity.services.MapService.MapApiWithMapData;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@SpringBootTest
class BiologicalProductivityApplicationTests {
	@Autowired
	RegionService regionService;
	@Autowired
	MapInfoService mapInfoService;
	static {
		nu.pattern.OpenCV.loadLocally();
		System.out.println("Load library");
	}
	@Test
	void test1() throws IOException {
		MapUploadService m = new MapUploadService(regionService,mapInfoService);
		m.checkMapsDirectory();
	}
	@Test
	void test2() throws IOException {
		MapApiWithMapData api = new MapApiWithMapData("region_1",regionService);
		Mat im1 = api.getSector(26,TypeMap.ZM).toHeatMap();
//		Imgcodecs.imwrite("./sectors/mfd.jpeg",im1);
	}
	@Test
	void test3() throws IOException {
		MapDeploymentImpl mapDeploymentImpl = new MapDeploymentImpl(regionService,"region_1");
		mapDeploymentImpl.deployMap(TypeMap.ZM);
//		Imgcodecs.imwrite("./sectors/mfd.jpeg",im1);
	}
	@Test
	void test5() throws IOException, InterruptedException {
		Instant start = Instant.now();
		
		MapDeployment mapDeployment = new MapDeploymentParall(regionService,"region_1");
		mapDeployment.deployMap(TypeMap.BP);
		
		Instant finish = Instant.now();
		System.out.println("Oбщее время выполнения "
				+ Duration.between(start,finish).toMillis());
	}

	
}
