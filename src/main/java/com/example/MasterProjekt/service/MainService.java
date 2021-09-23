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
import com.example.MasterProjekt.pojo.Technology;
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

    public Map<YearMonth, List<Technology>> getAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        long startTimeBQ = System.currentTimeMillis();
        Map<YearMonth, List<Technology>> technologiesInPeriodForCountry = bigQueryService
                .getTechnologiesInPeriodForCountry(startDate, endDate, countryCode);
        long endTimeBQ = System.currentTimeMillis();
        System.out.println("BigQueryPart finished");
        System.out.println("Big Query query took: " + (endTimeBQ - startTimeBQ) / 60000 + " mins.");

        long startTimeProcessing = System.currentTimeMillis();
        Set<SoftwareAndVersion> allSoftwaresAndVersions = new HashSet<SoftwareAndVersion>();
        Set<String> allSoftwares = new HashSet<String>();

        // get all software and versions from every Technologie
        for (List<Technology> techlist : technologiesInPeriodForCountry.values()) {
            for (Technology tech : techlist) {
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
            if (softwareAndVersionWithPossibleVul != null) {
                vulnerableSoftwaresAndVersionsSet.add(softwareAndVersionWithPossibleVul);
            }
        }

        // create new Map<YearMonth, List<Technology>> containing vulnerable
        // technologies
        List<SoftwareAndVersion> vulnerableSoftwaresAndVersionsList = new ArrayList<>(
                vulnerableSoftwaresAndVersionsSet);
        Map<YearMonth, List<Technology>> vulnearbleTechnologiesInPeriodForCountry = new HashMap<YearMonth, List<Technology>>();

        for (var entry : technologiesInPeriodForCountry.entrySet()) {
            List<Technology> vulTechs = new ArrayList<Technology>();

            for (Technology tech : entry.getValue()) {
                SoftwareAndVersion softwareAndVersion = tech.getSoftwareAndVersion();
                if (vulnerableSoftwaresAndVersionsList.contains(softwareAndVersion)) {
                    int index = vulnerableSoftwaresAndVersionsList.indexOf(softwareAndVersion);
                    Technology vulTechnologie = tech;
                    vulTechnologie.setSoftwareAndVersion(vulnerableSoftwaresAndVersionsList.get(index));
                    vulTechs.add(vulTechnologie);
                }
            }

            vulnearbleTechnologiesInPeriodForCountry.put(entry.getKey(), vulTechs);

        }

        long endTimeProcessing = System.currentTimeMillis();
        System.out.println("Processing took: " + (endTimeProcessing - startTimeProcessing) / 1000 + " secs.");

        return vulnearbleTechnologiesInPeriodForCountry;
    }

    public Map<YearMonth, Integer> getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Map<YearMonth, List<Technology>> technologiesWithVulnerabilitiesInPeriodForCountry = getAllVulnerabilitiesForCountryCodeAndIntervall(
                startDate, endDate, countryCode);
        Map<YearMonth, Integer> amountOftechnologiesWithVulnerabilitiesInPeriodForCountry = new HashMap<YearMonth, Integer>();

        for (var entry : technologiesWithVulnerabilitiesInPeriodForCountry.entrySet()) {
            int amountOfVulnerableTechnologies = 0;
            amountOfVulnerableTechnologies = entry.getValue().size();

            amountOftechnologiesWithVulnerabilitiesInPeriodForCountry.put(entry.getKey(),
                    amountOfVulnerableTechnologies);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("The whole process took: " + (endTime - startTime) / 60000 + " mins.");

        return amountOftechnologiesWithVulnerabilitiesInPeriodForCountry;
    }
}