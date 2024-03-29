package com.RW.BiologicalProductivity.services.DB.repos;

import com.RW.BiologicalProductivity.services.DB.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepo extends JpaRepository<Region,Long> {
    Optional<Region> findByName(String name);
}
