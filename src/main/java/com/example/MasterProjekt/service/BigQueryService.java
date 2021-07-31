package com.example.MasterProjekt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.example.MasterProjekt.pojo.Technologie;
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
        File credentialsPath = new File("D://Projekte//Master-projekt//key.json");

        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
        }

        bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).setProjectId(credentials.getProjectId())
                .build().getService();
    }

    public List<Technologie> exampleQuery() throws InterruptedException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(
                "SELECT * FROM `httparchive.technologies.2016_01_01_desktop` where url like '%.com/' AND info != '' limit 10")
                .setUseLegacySql(false).build();

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
        TableResult tableResult = queryJob.getQueryResults();

        return tableResultToTechnologieList(tableResult);
    }

    public Map<YearMonth, Integer> getTechnologiesInPeriodForCountry(String startDate, String endDate,
            String countryCode) throws JobException, InterruptedException {
        // Month, Amount
        Map<YearMonth, Integer> amountOfVulnerabilitesPerMonth = new TreeMap<YearMonth, Integer>();

        Map<YearMonth, String> querys = queryBuilder.getVulnerabilitiesInPeriodForCountryQuery(startDate, endDate,
                countryCode);

        Map<YearMonth, List<Technologie>> technologiesForEachMonth = executeAndProcessQueryPeriodForCountry(querys);
        // Bis hierhin getTechnologiesInPeriodForCountry danach AmountInPeriodFprCountry

        for (var entry : technologiesForEachMonth.entrySet()) {
            amountOfVulnerabilitesPerMonth.put(entry.getKey(), entry.getValue().size());
        }

        return amountOfVulnerabilitesPerMonth;
    }

    private Map<YearMonth, List<Technologie>> executeAndProcessQueryPeriodForCountry(Map<YearMonth, String> querys)
            throws JobException, InterruptedException {
        Map<YearMonth, List<Technologie>> technologiesForEachMonth = new HashMap<YearMonth, List<Technologie>>();

        for (var entry : querys.entrySet()) {
            technologiesForEachMonth.put(entry.getKey(),
                    tableResultToTechnologieList(executeSingleQuery(entry.getValue())));
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

    private List<Technologie> tableResultToTechnologieList(TableResult result) {
        List<Technologie> techlist = new ArrayList<Technologie>();

        Iterable<FieldValueList> fieldValueList = result.getValues();
        for (FieldValueList fieldValue : fieldValueList) {
            Technologie techToBeAdded = new Technologie(fieldValue.get(0).getStringValue(),
                    fieldValue.get(1).getStringValue(), fieldValue.get(2).getStringValue(),
                    fieldValue.get(3).getStringValue());
            techlist.add(techToBeAdded);
        }

        return techlist;
    }
}