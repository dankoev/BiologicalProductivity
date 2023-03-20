package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.MapData;

import com.RW.BiologicalProductivity.services.MapElementData;
import com.RW.BiologicalProductivity.services.ServicesAPI;

import com.RW.BiologicalProductivity.services.MapsManipulationImpl;
import com.RW.BiologicalProductivity.services.enums.TypeMap;
import com.RW.BiologicalProductivity.services.interfaces.MapsManipulation;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class BiologicalProductivityApplicationTests extends ServicesAPI {

	@Test
	void contextLoads() throws IOException {

		List<MapElementData> sectorData = getSector(26,TypeMap.ZM).mapDataList;
		List<MapElementData> sectorData2 = getSector(26,TypeMap.ZM).mapDataList;
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
	@Test
	void test1() throws IOException {
		Mat newM;
		Instant start = Instant.now();
		newM = ServicesAPI.getHeatMap(28, TypeMap.BP);
		HighGui.imshow("sdfs", newM);
		Instant finish = Instant.now();
	}
	@Test
	void JSONParce() throws ParseException, FileNotFoundException,IOException {
		FileReader tileInfo = new FileReader("./hotspot_data/tile_x_1_y_1_z_9.json");
		Object obj = new JSONParser().parse(tileInfo);
		JSONObject jsonObject = (JSONObject) obj;
		String str = jsonObject.toJSONString();
		System.out.println(str);
	}
	
}
