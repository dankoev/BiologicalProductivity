package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.MapData;
import com.RW.BiologicalProductivity.services.MapElementData;
import com.RW.BiologicalProductivity.services.ServicesAPI;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class BiologicalProductivityApplicationTests extends ServicesAPI {

	@Test
	void contextLoads() {

		List<MapElementData> sectorData = getSector(26,1).mapDataList;
		List<MapElementData> sectorData2 = getSector(26,3).mapDataList;
		List<MapElementData> result = new ArrayList<>();
		double value;
		
		Instant start = Instant.now();
		
		for (int i = 0 ; i < sectorData.size(); i++) {
			MapElementData mapElementData = new MapElementData();
			value = sectorData.get(i).value * sectorData2.get(i).value;
			mapElementData.setData(value,sectorData.get(i).x,sectorData.get(i).y);
			result.add(mapElementData);
		}
		Instant finish = Instant.now();
		
		long elapsed = Duration.between(start, finish).toMillis();
		System.out.println("Прошло времени, мс: " + elapsed);
	}

}
