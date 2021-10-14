package com.example.MasterProjekt.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.example.MasterProjekt.model.Vulnerability;
import com.example.MasterProjekt.pojo.SoftwareAndVersion;
import com.example.MasterProjekt.pojo.Technology;
import com.example.MasterProjekt.util.ScoreType;
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
        System.out.println("Big Query query took: " + (endTimeBQ - startTimeBQ) / 1000 + " secs.");

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
        Map<YearMonth, List<Technology>> vulnerableTechnologiesInPeriodForCountry = new TreeMap<YearMonth, List<Technology>>();

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

            vulnerableTechnologiesInPeriodForCountry.put(entry.getKey(), vulTechs);

        }

        long endTimeProcessing = System.currentTimeMillis();
        System.out.println("Processing took: " + (endTimeProcessing - startTimeProcessing) / 1000 + " secs.");

        return vulnerableTechnologiesInPeriodForCountry;
    }

    public Map<YearMonth, Long> getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(String startDate,
            String endDate, String countryCode) throws JobException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Map<YearMonth, List<Technology>> technologiesWithVulnerabilitiesInPeriodForCountry = getAllVulnerabilitiesForCountryCodeAndIntervall(
                startDate, endDate, countryCode);

        Map<YearMonth, Long> amountOftechnologiesWithVulnerabilitiesInPeriodForCountry = countAmountOfVulnerabilitiesInPeriod(
                technologiesWithVulnerabilitiesInPeriodForCountry);

        long endTime = System.currentTimeMillis();
        System.out.println("The whole process took: " + (endTime - startTime) / 60000 + " mins.");

        return amountOftechnologiesWithVulnerabilitiesInPeriodForCountry;
    }

    public Map<YearMonth, List<Technology>> getAllVulnerabilitiesForCWEAndIntervall(String startDate, String endDate,
            String cwe) throws JobException, InterruptedException {

        long startTimeBQ = System.currentTimeMillis();
        Map<YearMonth, List<Technology>> technologiesInPeriod = bigQueryService.getTechnologiesInPeriod(startDate,
                endDate);
        long endTimeBQ = System.currentTimeMillis();
        System.out.println("BigQueryPart finished");
        System.out.println("Big Query query took: " + (endTimeBQ - startTimeBQ) / 1000 + " secs.");

        long startTimeProcessing = System.currentTimeMillis();
        Set<SoftwareAndVersion> allSoftwaresAndVersions = new HashSet<SoftwareAndVersion>();
        Set<String> allSoftwares = new HashSet<String>();

        // get all software and versions from every Technologie
        for (List<Technology> techlist : technologiesInPeriod.values()) {
            for (Technology tech : techlist) {
                allSoftwares.add(tech.getSoftwareAndVersion().getSoftware().toLowerCase());
                allSoftwaresAndVersions.add(tech.getSoftwareAndVersion());
            }
        }

        // get all Vulnerabilites for given CWE
        List<Vulnerability> allVulnerabilitiesForCWE = vulnerabilityService.getAllVulnerabilitiesForCWE(cwe);

        // add vulnerabilities to all software and versions
        Set<SoftwareAndVersion> vulnerableSoftwaresAndVersionsSet = new HashSet<SoftwareAndVersion>();
        for (SoftwareAndVersion softwareAndVersion : allSoftwaresAndVersions) {
            SoftwareAndVersion softwareAndVersionWithPossibleVul = vulnerabilityService
                    .setVulnerabilityForSoftwareAndVersionIfPresent(softwareAndVersion, allVulnerabilitiesForCWE);
            if (softwareAndVersionWithPossibleVul != null) {
                vulnerableSoftwaresAndVersionsSet.add(softwareAndVersionWithPossibleVul);
            }
        }

        // create new Map<YearMonth, List<Technology>> containing vulnerable
        // technologies
        List<SoftwareAndVersion> vulnerableSoftwaresAndVersionsList = new ArrayList<>(
                vulnerableSoftwaresAndVersionsSet);
        Map<YearMonth, List<Technology>> vulnearbleTechnologiesInPeriod = new TreeMap<YearMonth, List<Technology>>();

        for (var entry : technologiesInPeriod.entrySet()) {
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

            vulnearbleTechnologiesInPeriod.put(entry.getKey(), vulTechs);

        }

        long endTimeProcessing = System.currentTimeMillis();
        System.out.println("Processing took: " + (endTimeProcessing - startTimeProcessing) / 1000 + " secs.");

        return vulnearbleTechnologiesInPeriod;
    }

    public Map<YearMonth, Long> getAmountOfAllVulnerabilitiesForCWEAndIntervall(String startDate, String endDate,
            String cwe) throws JobException, InterruptedException {

        long startTime = System.currentTimeMillis();

        Map<YearMonth, List<Technology>> technologiesWithCWEInPerioid = getAllVulnerabilitiesForCWEAndIntervall(
                startDate, endDate, cwe);

        Map<YearMonth, Long> amountOfTechnologiesWithVulnerabilitiesInPeriod = countAmountOfVulnerabilitiesInPeriod(
                technologiesWithCWEInPerioid);

        long endTime = System.currentTimeMillis();
        System.out.println("The whole process took: " + (endTime - startTime) / 60000 + " mins.");

        return amountOfTechnologiesWithVulnerabilitiesInPeriod;
    }

    public Map<YearMonth, List<Technology>> getAllVulnerabilitiesInPeriodForMatchingScore(String startDate,
            String endDate, Double lowerLimit, Double upperLimit, ScoreType score)
            throws JobException, InterruptedException {

        long startTimeBQ = System.currentTimeMillis();
        Map<YearMonth, List<Technology>> technologiesInPeriod = bigQueryService.getTechnologiesInPeriod(startDate,
                endDate);
        long endTimeBQ = System.currentTimeMillis();
        System.out.println("BigQueryPart finished");
        System.out.println("Big Query query took: " + (endTimeBQ - startTimeBQ) / 1000 + " secs.");

        long startTimeProcessing = System.currentTimeMillis();
        Set<SoftwareAndVersion> allSoftwaresAndVersions = new HashSet<SoftwareAndVersion>();
        Set<String> allSoftwares = new HashSet<String>();

        // get all software and versions from every Technologie
        for (List<Technology> techlist : technologiesInPeriod.values()) {
            for (Technology tech : techlist) {
                allSoftwares.add(tech.getSoftwareAndVersion().getSoftware().toLowerCase());
                allSoftwaresAndVersions.add(tech.getSoftwareAndVersion());
            }
        }

        // get all Vulnerabilites for given Score and ScoreType
        List<Vulnerability> allVulnerabilitiesMatchingScore;
        if (score.equals(ScoreType.IMPACT)) {
            allVulnerabilitiesMatchingScore = vulnerabilityService.getAllVulnerabilitesForImpactScore(lowerLimit,
                    upperLimit);
        } else {
            allVulnerabilitiesMatchingScore = vulnerabilityService
                    .getAllVulnerabilitesForExploitabilityScore(lowerLimit, upperLimit);
        }

        // add vulnerabilities to all software and versions
        Set<SoftwareAndVersion> vulnerableSoftwaresAndVersionsSet = new HashSet<SoftwareAndVersion>();
        for (SoftwareAndVersion softwareAndVersion : allSoftwaresAndVersions) {
            SoftwareAndVersion softwareAndVersionWithPossibleVul = vulnerabilityService
                    .setVulnerabilityForSoftwareAndVersionIfPresent(softwareAndVersion,
                            allVulnerabilitiesMatchingScore);
            if (softwareAndVersionWithPossibleVul != null) {
                vulnerableSoftwaresAndVersionsSet.add(softwareAndVersionWithPossibleVul);
            }
        }

        // create new Map<YearMonth, List<Technology>> containing vulnerable
        // technologies
        List<SoftwareAndVersion> vulnerableSoftwaresAndVersionsList = new ArrayList<>(
                vulnerableSoftwaresAndVersionsSet);
        Map<YearMonth, List<Technology>> vulnearbleTechnologiesInPeriod = new TreeMap<YearMonth, List<Technology>>();

        for (var entry : technologiesInPeriod.entrySet()) {
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

            vulnearbleTechnologiesInPeriod.put(entry.getKey(), vulTechs);

        }

        long endTimeProcessing = System.currentTimeMillis();
        System.out.println("Processing took: " + (endTimeProcessing - startTimeProcessing) / 1000 + " secs.");

        return vulnearbleTechnologiesInPeriod;
    }

    public Map<YearMonth, Long> getAmountAllVulnerabilitiesInPeriodForMatchingScore(String startDate, String endDate,
            Double lowerLimit, Double upperLimit, ScoreType score) throws JobException, InterruptedException {
        long startTime = System.currentTimeMillis();

        Map<YearMonth, List<Technology>> technologiesWithCWEInPerioid = getAllVulnerabilitiesInPeriodForMatchingScore(
                startDate, endDate, lowerLimit, upperLimit, score);

        Map<YearMonth, Long> amountOfTechnologiesWithVulnerabilitiesInPeriod = countAmountOfVulnerabilitiesInPeriod(
                technologiesWithCWEInPerioid);

        long endTime = System.currentTimeMillis();
        System.out.println("The whole process took: " + (endTime - startTime) / 60000 + " mins.");

        return amountOfTechnologiesWithVulnerabilitiesInPeriod;
    }

    private Map<YearMonth, Long> countAmountOfVulnerabilitiesInPeriod(
            Map<YearMonth, List<Technology>> vulnerableTechnologiesInPeriod) {

        Map<YearMonth, Long> amountOfTechnologiesWithVulnerabilitiesInPeriod = new TreeMap<YearMonth, Long>();

        for (var entry : vulnerableTechnologiesInPeriod.entrySet()) {
            long amountOfVulnerableTechnologies = 0;
            amountOfVulnerableTechnologies = entry.getValue().size();

            amountOfTechnologiesWithVulnerabilitiesInPeriod.put(entry.getKey(), amountOfVulnerableTechnologies);
        }

        return amountOfTechnologiesWithVulnerabilitiesInPeriod;
    }
}