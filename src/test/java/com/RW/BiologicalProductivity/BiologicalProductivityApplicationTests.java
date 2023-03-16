package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.MapData;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@SpringBootTest
class BiologicalProductivityApplicationTests {
	static{
		nu.pattern.OpenCV.loadLocally();
		System.out.println("Load library");
		
	}
	@Test
	void contextLoads() throws IOException {
		
		
		MapData mapData = new MapData("mapsInfo/H_UTM.tif");
		MapData mapData2 = new MapData("mapsInfo/N_UTM.tif");
		Mat sectorData = mapData.getFillSector(26).sertorData;
		Mat sectorData2 = mapData2.getFillSector(26).sertorData;
		Mat result = new Mat(sectorData.rows(),sectorData.cols(),sectorData.type());
		double value;
		
		Instant start = Instant.now();
		for (int i = 0; i < sectorData.rows(); i++) {
			for (int j = 0; j < sectorData.cols(); j++) {
				value = sectorData.get(i,j)[0]*sectorData2.get(i,j)[0];
				result.put(i,j,value);
			}
		}
		Instant finish = Instant.now();
		
		long elapsed = Duration.between(start, finish).toMillis();
		System.out.println("Прошло времени, мс: " + elapsed);
	}
	@Test
	void test2() throws IOException {
		MapData mapData = new MapData("mapsInfo/H_UTM.tif");
		MapData mapData2 = new MapData("mapsInfo/N_UTM.tif");
		Mat sectorData = mapData.getFillSector(26).sertorData;
		Mat sectorData2 = mapData2.getFillSector(26).sertorData;
		Mat result = new Mat(sectorData.rows(),sectorData.cols(),sectorData.type());
		Instant start = Instant.now();
		result = sectorData2.mul(sectorData);
		Instant finish = Instant.now();
		long elapsed = Duration.between(start, finish).toMillis();
		System.out.println("Прошло времени, мс: " + elapsed);
		System.out.println(result.dump());
	}

}
