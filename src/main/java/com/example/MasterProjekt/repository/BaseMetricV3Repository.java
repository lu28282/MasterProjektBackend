package com.example.MasterProjekt.repository;

import com.example.MasterProjekt.model.BaseMetricV3;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMetricV3Repository extends JpaRepository<BaseMetricV3, Long> {
    
    BaseMetricV3 findBaseMetricV3ByExploitabilityScore(Double exploitabilityScore);
    
    BaseMetricV3 findBaseMetricV3ByImpactScore(Double impactScore);

}
