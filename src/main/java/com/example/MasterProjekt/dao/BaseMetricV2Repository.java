package com.example.MasterProjekt.dao;

import com.example.MasterProjekt.model.BaseMetricV2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMetricV2Repository extends CrudRepository<BaseMetricV2, Long> {
    
    BaseMetricV2 findBaseMetricV2ByExploitabilityScore(Double exploitabilityScore);
    
    BaseMetricV2 findBaseMetricV2ByImpactScore(Double impactScore);

}
