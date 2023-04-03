package com.RW.BiologicalProductivity.services.DB.Entities;

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
    
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MapInfo> mapsInfo = new HashSet<>();
}
