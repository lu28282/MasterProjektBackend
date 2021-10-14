package com.example.MasterProjekt.controller;

import java.time.YearMonth;
import java.util.Map;
import java.util.List;

import com.example.MasterProjekt.service.MainService;
import com.example.MasterProjekt.util.ScoreType;
import com.example.MasterProjekt.pojo.Technology;
import com.google.cloud.bigquery.JobException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    MainService mainService;

    @Autowired
    public AdminController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/test")
    private void test() {
        System.out.println("Hello World!");
    }

    @GetMapping("/technologies")
    private Map<YearMonth, List<Technology>> getAllVulnerabilitesInPeriodForCountry(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String countryCode) throws JobException, InterruptedException {
        return mainService.getAllVulnerabilitiesForCountryCodeAndIntervall(startDate, endDate, countryCode);
    }

    @GetMapping("/CWE")
    private Map<YearMonth, List<Technology>> getAllVulnerabilitiesInPerioidForCEW(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String cwe) throws JobException, InterruptedException {
        return mainService.getAllVulnerabilitiesForCWEAndIntervall(startDate, endDate, cwe);
    }

    @GetMapping("/impactScore")
    private Map<YearMonth, List<Technology>> getAllVulnerabilitiesInPeriodWithMatchingImpactScore(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam Double lowerLimit,
            @RequestParam Double upperLimit) throws JobException, InterruptedException {
        return mainService.getAllVulnerabilitiesInPeriodForMatchingScore(startDate, endDate, lowerLimit, upperLimit,
                ScoreType.IMPACT);
    }

    @GetMapping("/exploitabilityScore")
    private Map<YearMonth, List<Technology>> getAllVulnerabilitiesInPeriodWithMatchingExploitabilityScore(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam Double lowerLimit,
            @RequestParam Double upperLimit) throws JobException, InterruptedException {
        return mainService.getAllVulnerabilitiesInPeriodForMatchingScore(startDate, endDate, lowerLimit, upperLimit,
                ScoreType.EXPLOITABILITY);
    }
}
