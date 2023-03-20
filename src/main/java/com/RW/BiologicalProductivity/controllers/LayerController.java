package com.RW.BiologicalProductivity.controllers;

import com.google.gson.Gson;
import org.apache.catalina.loader.ResourceEntry;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Scanner;

@RestController
public class LayerController {

    @GetMapping("/hotspot_data/{z}/{x}/{y}")
    public String hotspotData(@PathVariable("z") long z,
                                @PathVariable("x") long x,
                                @PathVariable("y") long y,
                                @RequestParam(defaultValue = "") String callback) throws FileNotFoundException, IOException, ParseException {
        System.out.println(callback);
        FileReader tileInfo = new FileReader("./hotspot_data/tile_x_1_y_1_z_9.json");
        Object obj = new JSONParser().parse(tileInfo);
        JSONObject jsonObject = (JSONObject) obj;
        String data = jsonObject.toJSONString();
        String out = callback + "(" + data + ")";
        return out;
    }
// 
}
