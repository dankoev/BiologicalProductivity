package com.RW.BiologicalProductivity.services.DB.Entities;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.lang.reflect.Type;

@Entity
@NoArgsConstructor
@Table(name = "maps_info")
public class MapInfo {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private TypeMap type;
    double minValue;
    double maxValue;
    
    public MapInfo(String name, TypeMap type, double minValue, double maxValue) {
        this.name = name;
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Region getRegion() {
        return region;
    }
    
    public void setRegion(Region region) {
        this.region = region;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public TypeMap getType() {
        return type;
    }
    
    public void setType(TypeMap type) {
        this.type = type;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
