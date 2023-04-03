package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.models.RegionInfo;
import com.RW.BiologicalProductivity.services.MapService.MapAPI;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

@SpringBootTest
class BiologicalProductivityApplicationTests extends MapAPI {
	
	@Test
	void test1() throws IOException {
		Mat newM;
		Instant start = Instant.now();
		newM = MapAPI.getHeatMap(26, TypeMap.BP);
		HighGui.imshow("sdfs", newM);
		Instant finish = Instant.now();
	}
	@Test
	void test3() throws IOException {
		Mat newM;
		newM = MapAPI.getSector(26, TypeMap.ZM).data;
		boolean write  = Imgcodecs.imwrite("sector.tif",newM);
		System.out.println(write);
	}
	@Test
	void test2() throws IOException {
		String path = "sectors/region_1/data/RegionInfo.json";
		Gson gson = new Gson();
		RegionInfo regionInfo = gson.fromJson(new FileReader(path), RegionInfo.class);
		System.out.println("JSON прочитан для карты: "+ regionInfo.getName());
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
