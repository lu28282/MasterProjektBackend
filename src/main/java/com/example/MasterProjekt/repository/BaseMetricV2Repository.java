package com.example.MasterProjekt.repository;

import com.example.MasterProjekt.model.BaseMetricV2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMetricV2Repository extends JpaRepository<BaseMetricV2, Long> {
    
    BaseMetricV2 findBaseMetricV2ByExploitabilityScore(Double exploitabilityScore);
    
    BaseMetricV2 findBaseMetricV2ByImpactScore(Double impactScore);

}
