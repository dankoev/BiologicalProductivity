package com.RW.BiologicalProductivity.services.DB.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

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
    private double BottomLat;
    private double leftLong;
    private double rightLong;
    
    private int regionRows;
    private int regionCols;
    private int cvType;

    
    public Region(String name, String mapsRootPath) {
        this.name = name;
        this.mapsRootPath = mapsRootPath;
    }
    
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapInfo> mapsInfo = new HashSet<>();
    
    public void addMapsInfo(MapInfo info) {
        this.mapsInfo.add(info);
        info.setRegion(this);
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
        return BottomLat;
    }
    
    public void setBottomLat(double bottomLat) {
        BottomLat = bottomLat;
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
}
