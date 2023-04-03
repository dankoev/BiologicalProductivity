package com.RW.BiologicalProductivity.services.DB.Entities;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
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
    private TypeMap type;
    double minValue;
    double maxValue;
    
    public MapInfo(String name, TypeMap type, double minValue, double maxValue) {
        this.name = name;
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
