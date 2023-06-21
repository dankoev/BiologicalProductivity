package com.RW.BiologicalProductivity.services.MapService.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TypeMap {
    H, CFT, N, T,
    ZM, BetaH, SY, BP;
    public static TypeMap getTypeByName(String name){
        List<TypeMap> typeMaps = Arrays.asList(TypeMap.values());
        for(TypeMap item: typeMaps){
            if (name.contains(item.name())){
                return item;
            }
        }
        return null;
    }
    public static boolean isInitialMap(TypeMap typeMap){
        return typeMap == H || typeMap == CFT || typeMap == N || typeMap == T;
    }
    

}
