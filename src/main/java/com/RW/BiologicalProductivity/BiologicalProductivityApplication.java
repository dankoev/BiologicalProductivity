package com.RW.BiologicalProductivity;

import com.RW.BiologicalProductivity.services.DB.exceptions.DataBaseException;
import com.RW.BiologicalProductivity.services.DB.services.MapInfoService;
import com.RW.BiologicalProductivity.services.DB.services.RegionService;
import com.RW.BiologicalProductivity.services.MapService.Deployment.MapUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class
BiologicalProductivityApplication {
	@Autowired
	RegionService regionService;
	@Autowired
	MapInfoService mapInfoService;
	public static void main(String[] args) {
		SpringApplication.run(BiologicalProductivityApplication.class, args);
	}
	

}
