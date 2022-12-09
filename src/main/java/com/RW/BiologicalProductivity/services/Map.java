package com.RW.BiologicalProductivity.services;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import ch.qos.logback.core.pattern.FormatInfo;

public class Map {
    private static final double noDataValue = -99999.0;
    
    public Mat img;
    public int imgCols = 0;
    public int imgRows = 0;
    public int imgCh = 0;
    public int imgSize = 0;

    public Map(String path, int type){
        this.img = Imgcodecs.imread(path, type);
        this.imgCols = img.cols();
        this.imgRows = img.rows();
        this.imgCh = img.channels();
        this.imgSize = (int) (img.total() * this.imgCh);
    }
    public Mat getMap(){
        return img;
    }
    public  Mat getGrayMap(double minMapValue,double maxMapValue){
        Mat newImg = new Mat(imgRows, imgCols, CvType.CV_8U);
        int newvValue;
        double currValue;
        for (int x = 0; x < imgRows; x++) {
            for (int y = 0; y < imgCols; y++) {
                currValue = img.get(x,y)[0];
                newvValue = (int)(255 * (currValue - minMapValue) / maxMapValue);
                newImg.put(x, y , newvValue);
            }
            
        }
        return newImg;
    }

}