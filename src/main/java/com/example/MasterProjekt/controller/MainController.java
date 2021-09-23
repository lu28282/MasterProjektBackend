package com.example.MasterProjekt.controller;

import java.time.YearMonth;
import java.util.Map;

import com.example.MasterProjekt.service.MainService;
import com.google.cloud.bigquery.JobException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/test")
    private void test() {
        System.out.println("Hello World!");
    }

    @GetMapping("/technologies")
    private Map<YearMonth, Integer> getAllVulnerabilitesInPeriodForCountry(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String countryCode) throws JobException, InterruptedException {
        // return
        // mainService.getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall("2016_01",
        // "2017_01", "com");
        return mainService.getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall(startDate, endDate, countryCode);
    }

}
