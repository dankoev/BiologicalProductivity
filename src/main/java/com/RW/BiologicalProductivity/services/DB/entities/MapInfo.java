package com.RW.BiologicalProductivity.services.DB.entities;

import com.RW.BiologicalProductivity.services.MapService.enums.TypeMap;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    public MapInfo(String name, TypeMap type) {
        this.name = name;
        this.type = type;
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
    
}
