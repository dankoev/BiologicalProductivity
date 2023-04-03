package com.RW.BiologicalProductivity.services.DB.Repos;

import com.RW.BiologicalProductivity.services.DB.Entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepo extends JpaRepository<Region,Long> {
}
