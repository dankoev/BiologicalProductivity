package com.RW.BiologicalProductivity.services.DB.Repos;

import com.RW.BiologicalProductivity.services.DB.Entities.MapInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapInfoRepo extends JpaRepository<MapInfo,Long> {
}
