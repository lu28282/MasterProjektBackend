package com.example.MasterProjekt.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.MasterProjekt.model.Vulnerability;
import com.example.MasterProjekt.pojo.Technologie;
import com.google.cloud.bigquery.JobException;

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

    public Map<YearMonth, List<Technologie>> getAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        Map<YearMonth, List<Technologie>> technologiesInPeriodForCountry = bigQueryService
                .getTechnologiesInPeriodForCountry(startDate, endDate, countryCode);
        System.out.println("BigQueryPart finished");

        Set<String> allSoftwares = new HashSet<String>();

        for (List<Technologie> techlist : technologiesInPeriodForCountry.values()) {
            for (Technologie tech : techlist) {
                allSoftwares.add(tech.getApp().toLowerCase());
            }
        }

        List<Vulnerability> allVulnerabilitiesForEverySoftware = vulnerabilityService
                .getAllVulnerabilitiesForSoftwareSet(allSoftwares);

        Map<YearMonth, List<Technologie>> techsWithVulnerabilitiesInPeriodForCountry = new HashMap<YearMonth, List<Technologie>>();
        for (var entry : technologiesInPeriodForCountry.entrySet()) {
            List<Technologie> techsWithVuls = vulnerabilityService
                    .setVulnerabilityForTechnologieIfPresent(entry.getValue(), allVulnerabilitiesForEverySoftware);

            techsWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(), techsWithVuls);
        }

        return techsWithVulnerabilitiesInPeriodForCountry;
    }

    public Map<YearMonth, Integer> getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Map<YearMonth, List<Technologie>> technologiesWithVulnerabilitiesInPeriodForCountry = getAllVulnerabilitiesForCountryCodeAndIntervall(
                startDate, endDate, countryCode);
        Map<YearMonth, Integer> amountOftechnologiesWithVulnerabilitiesInPeriodForCountry = new HashMap<YearMonth, Integer>();

        for (var entry : technologiesWithVulnerabilitiesInPeriodForCountry.entrySet()) {
            int amountOfVulnerableTechnologies = 0;
            amountOfVulnerableTechnologies = entry.getValue().size();

            amountOftechnologiesWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(),
                    amountOfVulnerableTechnologies);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Processing the data takes: " + (endTime - startTime) / 60000 + " mins.");

        return amountOftechnologiesWithVulnerabilitiesInPeriodForCountry;
    }
}