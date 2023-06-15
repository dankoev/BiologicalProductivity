package com.RW.BiologicalProductivity.services.DB.repos;

import com.RW.BiologicalProductivity.services.DB.entities.MapInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapInfoRepo extends JpaRepository<MapInfo,Long> {
}
