package com.example.MasterProjekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {
    
    BigQueryService bigQueryService;

    VulnerabilityService vulnerabilityService;

    @Autowired
    public MainService(BigQueryService bigQueryService, VulnerabilityService vulnerabilityService) {
        this.bigQueryService = bigQueryService;
        this.vulnerabilityService = vulnerabilityService;
    }
}
