package com.example.MasterProjekt.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.MasterProjekt.model.Vulnerability;
import com.example.MasterProjekt.pojo.SoftwareAndVersion;
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

        Set<SoftwareAndVersion> allSoftwaresAndVersions = new HashSet<SoftwareAndVersion>();
        Set<String> allSoftwares = new HashSet<String>();

        // get all software and versions from every Technologie
        for (List<Technologie> techlist : technologiesInPeriodForCountry.values()) {
            for (Technologie tech : techlist) {
                allSoftwares.add(tech.getSoftwareAndVersion().getSoftware().toLowerCase());
                allSoftwaresAndVersions.add(tech.getSoftwareAndVersion());
            }
        }

        // get all Vulnerabilites for all softwares
        List<Vulnerability> allVulnerabilitiesForEverySoftware = vulnerabilityService
                .getAllVulnerabilitiesForSoftwareSet(allSoftwares);

        // add vulnerabilities to all software and versions
        Set<SoftwareAndVersion> vulnerableSoftwaresAndVersionsSet = new HashSet<SoftwareAndVersion>();
        for (SoftwareAndVersion softwareAndVersion : allSoftwaresAndVersions) {
            SoftwareAndVersion softwareAndVersionWithPossibleVul = vulnerabilityService
                    .setVulnerabilityForSoftwareAndVersionIfPresent(softwareAndVersion,
                            allVulnerabilitiesForEverySoftware);
            if (softwareAndVersionWithPossibleVul.getVulnerabilities().size() > 0) {
                vulnerableSoftwaresAndVersionsSet.add(softwareAndVersionWithPossibleVul);
            }
        }

        List<SoftwareAndVersion> vulnerableSoftwaresAndVersionsList = new ArrayList<>(
                vulnerableSoftwaresAndVersionsSet);
        Map<YearMonth, List<Technologie>> vulnearbleTechnologiesInPeriodForCountry = new HashMap<YearMonth, List<Technologie>>();
        for (var entry : technologiesInPeriodForCountry.entrySet()) {
            List<Technologie> vulTechs = new ArrayList<Technologie>();

            for (Technologie tech : entry.getValue()) {
                SoftwareAndVersion softwareAndVersion = tech.getSoftwareAndVersion();
                if (vulnerableSoftwaresAndVersionsList.contains(softwareAndVersion)) {
                    int index = vulnerableSoftwaresAndVersionsList.indexOf(softwareAndVersion);
                    Technologie vulTechnologie = tech;
                    vulTechnologie.setSoftwareAndVersion(vulnerableSoftwaresAndVersionsList.get(index));
                    vulTechs.add(vulTechnologie);
                }
            }

            vulnearbleTechnologiesInPeriodForCountry.put(entry.getKey(), vulTechs);

        }

        return vulnearbleTechnologiesInPeriodForCountry;

        // List<Vulnerability> allVulnerabilitiesForEverySoftware = vulnerabilityService
        // .getAllVulnerabilitiesForSoftwareSet(allSoftwares);

        // Map<YearMonth, List<Technologie>> techsWithVulnerabilitiesInPeriodForCountry
        // = new HashMap<YearMonth, List<Technologie>>();
        // for (var entry : technologiesInPeriodForCountry.entrySet()) {
        // List<Technologie> techsWithVuls = vulnerabilityService
        // .setVulnerabilityForTechnologieIfPresent(entry.getValue(),
        // allVulnerabilitiesForEverySoftware);

        // techsWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(),
        // techsWithVuls);
        // }

        // return techsWithVulnerabilitiesInPeriodForCountry;
    }

    public Map<YearMonth, Integer> getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        long startTime = System.currentTimeMillis();

        // Map<YearMonth, List<Technologie>> technologiesWithVulnerabilitiesInPeriodForCountry = getAllVulnerabilitiesForCountryCodeAndIntervall(
        //         startDate, endDate, countryCode);
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