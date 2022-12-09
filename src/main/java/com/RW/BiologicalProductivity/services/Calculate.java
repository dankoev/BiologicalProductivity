package com.RW.BiologicalProductivity.services;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

class Calculate {
    static{
        nu.pattern.OpenCV.loadLocally();
        System.out.println("Load library");
    }
    public static void main(String[] args) {

        Map map = new Map("dataMaps/H_UTM.tif", Imgcodecs.IMREAD_UNCHANGED | Imgcodecs.IMREAD_ANYDEPTH | Imgcodecs.IMREAD_LOAD_GDAL);
        HighGui.namedWindow("Input", HighGui.WINDOW_AUTOSIZE);

        Mat newMat = map.getGrayMap(206, 1300);
        HighGui.imshow("Input", newMat);
        HighGui.waitKey();
        
    }
    
}