package com.RW.BiologicalProductivity.models;

import lombok.Data;

import java.util.List;

@Data
public class PolygonInfo {
        private String name;
        private List<Double[]> coordinates;
        private String typeMap;
}
