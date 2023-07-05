package com.RW.BiologicalProductivity.services.GDAL;

import org.gdal.gdal.gdal;
import org.gdal.ogr.ogr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GdalServiceTest {

    @Test
    void testDriverRegister() {
        ogr.RegisterAll();
    }

    @Test
    void testLoadGdal() {
        gdal.AllRegister();
    }
}