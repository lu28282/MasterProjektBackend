package com.example.MasterProjekt.controller;

import java.time.YearMonth;
import java.util.Map;

import com.example.MasterProjekt.service.MainService;
import com.example.MasterProjekt.util.ScoreType;
import com.google.cloud.bigquery.JobException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MainController {

    MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/test")
    private String test() {
        String s = "Successful test!";
        System.out.println("Hello World!");
        return s;
    }

    @GetMapping("/technologies")
    private Map<YearMonth, Long> getAllVulnerabilitesInPeriodForCountry(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String countryCode) throws JobException, InterruptedException {
        return mainService.getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(startDate, endDate, countryCode);
    }

    @GetMapping("/CWE")
    private Map<YearMonth, Long> getAllVulnerabilitiesInPerioidForCEW(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String cwe) throws JobException, InterruptedException {
        return mainService.getAmountOfAllVulnerabilitiesForCWEAndIntervall(startDate, endDate, cwe);
    }

    @GetMapping("/impactScore")
    private Map<YearMonth, Long> getAllVulnerabilitiesInPeriodWithMatchingImpactScore(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam Double lowerLimit, @RequestParam Double upperLimit)
            throws JobException, InterruptedException {
        return mainService.getAmountAllVulnerabilitiesInPeriodForMatchingScore(startDate, endDate, lowerLimit,
                upperLimit, ScoreType.IMPACT);
    }

    @GetMapping("/exploitabilityScore")
    private Map<YearMonth, Long> getAllVulnerabilitiesInPeriodWithMatchingExploitabilityScore(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam Double lowerLimit,
            @RequestParam Double upperLimit) throws JobException, InterruptedException {
        return mainService.getAmountAllVulnerabilitiesInPeriodForMatchingScore(startDate, endDate, lowerLimit,
                upperLimit, ScoreType.EXPLOITABILITY);
    }

}
