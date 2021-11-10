package com.example.MasterProjekt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.MasterProjekt.pojo.SoftwareAndVersion;
import com.example.MasterProjekt.pojo.Technology;
import com.example.MasterProjekt.util.QueryBuilder;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BigQueryService {

    private final BigQuery bigQuery;

    @Autowired
    private QueryBuilder queryBuilder;

    public BigQueryService(@Value("${google.api.key.location}") final String filePath) throws IOException {
        ServiceAccountCredentials credentials;
        File credentialsPath = new File(filePath);

        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
        }

        bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).setProjectId(credentials.getProjectId())
                .build().getService();
    }

    // public List<Technology> exampleQuery() throws InterruptedException {
    //     QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(
    //             "SELECT * FROM `httparchive.technologies.2016_01_01_desktop` where url like '%.com/' AND info != '' limit 10")
    //             .setUseLegacySql(false).build();

    //     // Create a job ID so that we can safely retry.
    //     JobId jobId = JobId.of(UUID.randomUUID().toString());
    //     Job queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    //     // Wait for the query to complete.
    //     queryJob = queryJob.waitFor();

    //     // Check for errors
    //     if (queryJob == null) {
    //         throw new RuntimeException("Job no longer exists");
    //     } else if (queryJob.getStatus().getError() != null) {
    //         // You can also look at queryJob.getStatus().getExecutionErrors() for all
    //         // errors, not just the latest one.
    //         throw new RuntimeException(queryJob.getStatus().getError().toString());
    //     }

    //     // Get the results.
    //     TableResult tableResult = queryJob.getQueryResults();

    //     return tableResultToTechnologieList(tableResult);
    // }

    public Map<YearMonth, List<Technology>> getTechnologiesInPeriodForCountry(String startDate, String endDate,
            String countryCode) throws JobException, InterruptedException {

        Map<YearMonth, String> querys = queryBuilder.getTechnologiesInPeriodForCountryQuery(startDate, endDate,
                countryCode);

        Map<YearMonth, List<Technology>> technologiesForEachMonth = executeAndProcessQueryPeriodForCountry(querys);

        return technologiesForEachMonth;
    }

    public Map<YearMonth, List<Technology>> getTechnologiesInPeriod(String startDate, String endDate)
            throws JobException, InterruptedException {

        Map<YearMonth, String> querys = queryBuilder.getTechnologiesInPeriod(startDate, endDate);

        Map<YearMonth, List<Technology>> technologiesForEachMonth = executeAndProcessQueryPeriodForCountry(querys);

        return technologiesForEachMonth;
    }

    private Map<YearMonth, List<Technology>> executeAndProcessQueryPeriodForCountry(Map<YearMonth, String> querys)
            throws JobException, InterruptedException {
        Map<YearMonth, List<Technology>> technologiesForEachMonth = new HashMap<YearMonth, List<Technology>>();

        for (var monthEntry : querys.entrySet()) {
            technologiesForEachMonth.put(monthEntry.getKey(),
                    tableResultToTechnologieList(executeSingleQuery(monthEntry.getValue())));
        }

        return technologiesForEachMonth;
    }

    private TableResult executeSingleQuery(String query) throws JobException, InterruptedException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).setUseLegacySql(false).build();

        // Create a job ID so that we can safely retry.
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        // Wait for the query to complete.
        queryJob = queryJob.waitFor();

        // Check for errors
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }

        // Get the results.
        return queryJob.getQueryResults();
    }

    private List<Technology> tableResultToTechnologieList(TableResult result) {
        List<Technology> techlist = new ArrayList<Technology>();

        Iterable<FieldValueList> fieldValueList = result.getValues();
        for (FieldValueList fieldValue : fieldValueList) {
            SoftwareAndVersion softwareAndVersion = new SoftwareAndVersion(fieldValue.get(2).getStringValue(),
                    fieldValue.get(3).getStringValue());
            Technology techToBeAdded = new Technology(fieldValue.get(0).getStringValue(),
                    fieldValue.get(1).getStringValue(), softwareAndVersion);
            techlist.add(techToBeAdded);
        }

        return techlist;
    }
}