package com.example.MasterProjekt.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.example.MasterProjekt.pojo.Technologie;
import com.example.MasterProjekt.service.BigQueryService;
import com.example.MasterProjekt.util.QueryBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.bigquery.JobException;

@RestController
public class MainController {

    @Autowired
    BigQueryService bigQueryService;

    @Autowired
    private QueryBuilder queryBuilder;

    @GetMapping("/getGoogle")
    private String getGoogle() throws Exception {
        List<Technologie> techlist = bigQueryService.exampleQuery();
        techlist.stream().forEach(tech -> System.out.println(tech.toString()));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(techlist);
        return json;
    }

    @GetMapping("/test")
    private void test() {
        System.out.println("Hello World!");
    }

    @GetMapping("/testQuery")
    private void test1() {
        queryBuilder.getVulnerabilitiesInPeriodForCountryQuery("2016_05", "2016_05", "de");
    }

    @GetMapping("/testCountryQuery")
    private void test2() throws JobException, InterruptedException {
        Map<YearMonth, Integer> technologiesInPeriodForCountry = bigQueryService
                .getTechnologiesInPeriodForCountry("2016_12", "2016_12", "de");

        technologiesInPeriodForCountry.entrySet().stream()
                .forEach(x -> System.out.println(x.getKey().toString().replace("-", "_") + ": " + x.getValue()));
        // ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        // String json = ow.writeValueAsString(vulnerabilitiesInPeriodForCountry);
        // return json;
    }
}
