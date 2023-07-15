package com.RW.BiologicalProductivity.services.DB.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "regions")
public class Region {
    @Id
    private String name;
    
    private String mapsRootPath;
    private double topLat;
    private double bottomLat;
    private double leftLong;
    private double rightLong;
    
    private boolean filled;
    
    private int regionRows;
    private int regionCols;
    private double[] transform;
    private int cvType;
    
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapInfo> mapsInfo = new HashSet<>();
    
    public Region(String name, String mapsRootPath) {
        this.name = name;
        this.mapsRootPath = mapsRootPath;
    }
    public void addMapsInfo(MapInfo info) {
        this.mapsInfo.add(info);
        info.setRegion(this);
    }
    public Region clone(){
        Region region = new Region(this.name, this.mapsRootPath);
        region.topLat = this.topLat;
        region.bottomLat = this.bottomLat;
        region.leftLong = this.leftLong;
        region.rightLong = this.rightLong;
        region.filled = this.filled;
        region.regionCols = this.regionCols;
        region.regionRows = this.regionRows;
        region.cvType = this.cvType;
        region.setMapsInfo(new HashSet<>(this.mapsInfo));
        return this;
    }
    
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMapsRootPath() {
        return mapsRootPath;
    }
    
    public void setMapsRootPath(String mapsRootPath) {
        this.mapsRootPath = mapsRootPath;
    }
    
    public double getTopLat() {
        return topLat;
    }
    
    public void setTopLat(double topLat) {
        this.topLat = topLat;
    }
    
    public double getBottomLat() {
        return bottomLat;
    }
    
    public void setBottomLat(double bottomLat) {
        this.bottomLat = bottomLat;
    }
    
    public double getLeftLong() {
        return leftLong;
    }
    
    public void setLeftLong(double leftLong) {
        this.leftLong = leftLong;
    }
    
    public double getRightLong() {
        return rightLong;
    }
    
    public void setRightLong(double rightLong) {
        this.rightLong = rightLong;
    }
    
    public Set<MapInfo> getMapsInfo() {
        return mapsInfo;
    }
    
    public void setMapsInfo(Set<MapInfo> mapsInfo) {
        this.mapsInfo = mapsInfo;
    }
    
    public int getRegionRows() {
        return regionRows;
    }
    
    public void setRegionRows(int regionRows) {
        this.regionRows = regionRows;
    }
    
    public int getRegionCols() {
        return regionCols;
    }
    
    public void setRegionCols(int regionCols) {
        this.regionCols = regionCols;
    }
    
    public int getCvType() {
        return cvType;
    }
    
    public void setCvType(int cvType) {
        this.cvType = cvType;
    }
    
    public boolean isFilled() {
        return filled;
    }
    
    public void setFilled(boolean filled) {
        this.filled= filled;
    }
    public double[] getTransform() {
        return transform;
    }
    
    public void setTransform(double[] transform) {
        this.transform = transform;
    }
    public void addMapInfo(MapInfo mapInfo) {
        mapsInfo.add(mapInfo);
        mapInfo.setRegion(this);
    }
    public void removeMapInfo(MapInfo mapInfo) {
        mapsInfo.remove(mapInfo);
        mapInfo.setRegion(null);
    }
}
