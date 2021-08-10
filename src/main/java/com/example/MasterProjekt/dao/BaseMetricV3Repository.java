package com.example.MasterProjekt.dao;

import com.example.MasterProjekt.model.BaseMetricV3;

import org.springframework.data.repository.CrudRepository;

public interface BaseMetricV3Repository extends CrudRepository<BaseMetricV3, Long> {
    
    BaseMetricV3 findBaseMetricV3ByExploitabilityScore(Double exploitabilityScore);
    
    BaseMetricV3 findBaseMetricV3ByImpactScore(Double impactScore);

}
