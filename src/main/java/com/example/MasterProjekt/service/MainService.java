package com.example.MasterProjekt.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<YearMonth, List<Technologie>> technologiesWithVulnerabilitiesInPeriodForCountry = new HashMap<YearMonth, List<Technologie>>();

        for (var entry : technologiesInPeriodForCountry.entrySet()) {
            System.out.println("Monat: " + entry.getKey());
            List<Technologie> technologies = entry.getValue();
            System.out.println("Technologie size: " + technologies.size());
            List<Technologie> technologiesWithVulnerabilites = new ArrayList<Technologie>();
            int index = 0;
            for (Technologie technologie : technologies) {
                System.out.println("Inside Technologies: " + index);
                List<Vulnerability> vulnerabilities = vulnerabilityService
                        .getAllVulnerabilitiesBySoftwareAndVersion(technologie.getApp(), technologie.getVersion());
                System.out.println("After getAllVulnerabilites " + index);
                technologie.setVulnerabilities(vulnerabilities);
                System.out.println("Set Vulnerabilities " + index);

                if (vulnerabilities.size() > 0) {
                    technologiesWithVulnerabilites.add(technologie);
                }
                index++;
            }

            technologiesWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(), technologiesWithVulnerabilites);
        }

        System.out.println("Durch mit getAllVuls");
        System.out.println(technologiesWithVulnerabilitiesInPeriodForCountry);
        return technologiesWithVulnerabilitiesInPeriodForCountry;
    }

    public Map<YearMonth, Integer> getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        Map<YearMonth, List<Technologie>> technologiesWithVulnerabilitiesInPeriodForCountry = getAllVulnerabilitiesForCountryCodeAndIntervall(
                startDate, endDate, countryCode);
        Map<YearMonth, Integer> amountOftechnologiesWithVulnerabilitiesInPeriodForCountry = new HashMap<YearMonth, Integer>();

        for (var entry : technologiesWithVulnerabilitiesInPeriodForCountry.entrySet()) {
            int amountOfVulnerableTechnologies = 0;
            amountOfVulnerableTechnologies = entry.getValue().size();

            amountOftechnologiesWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(),
                    amountOfVulnerableTechnologies);
        }

        return amountOftechnologiesWithVulnerabilitiesInPeriodForCountry;
    }
}