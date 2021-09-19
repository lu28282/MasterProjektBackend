package com.example.MasterProjekt.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.example.MasterProjekt.model.Vulnerability;
import com.example.MasterProjekt.service.MainService;
import com.google.cloud.bigquery.JobException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    // @Autowired
    // BigQueryService bigQueryService;

    // @GetMapping("/getGoogle")
    // private String getGoogle() throws Exception {
    // List<Technologie> techlist = bigQueryService.exampleQuery();
    // techlist.stream().forEach(tech -> System.out.println(tech.toString()));

    // ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    // String json = ow.writeValueAsString(techlist);
    // return json;
    // }

    @GetMapping("/test")
    private void test() {
        System.out.println("Hello World!");
    }

    @GetMapping("/test1")
    private Map<YearMonth, Integer> getAllVulnerabilitesInPeriodForCountry() throws JobException, InterruptedException {
        return mainService.getAmountOfAllVulnerabilitiesForCountryCodeAndIntervall("2016_01", "2018_04", "com");
    }

    // @GetMapping("/testCountryQuery")
    // private void test2() throws JobException, InterruptedException {
    // Map<YearMonth, Integer> technologiesInPeriodForCountry = bigQueryService
    // .getTechnologiesInPeriodForCountry("2016_12", "2016_12", "de");

    // technologiesInPeriodForCountry.entrySet().stream()
    // .forEach(x -> System.out.println(x.getKey().toString().replace("-", "_") + ":
    // " + x.getValue()));
    // }
}
