package com.example.MasterProjekt.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class QueryBuilder {

    public Map<YearMonth, String> getTechnologiesInPeriodForCountryQuery(String startDate, String endDate,
            String countryCode) {
        Map<YearMonth, String> queryForEachMonth = new HashMap<YearMonth, String>();

        Map<YearMonth, List<String>> monthMap = generateMapOfMonthToQueryOver(startDate, endDate);

        queryForEachMonth = generateQueryForEachMonthInPeriodForCountry(monthMap, countryCode);

        // queryForEachMonth.entrySet().forEach(x -> System.out.println(x.getKey() + ":
        // " + x.getValue()));

        return queryForEachMonth;
    }

    private Map<YearMonth, List<String>> generateMapOfMonthToQueryOver(String startDateString, String endDateString) {
        // <Month of a Year, Month of a Year concatenated with _01_desktop and
        // _15_desktop to match the BigQuery-table-names>
        Map<YearMonth, List<String>> monthMap = new HashMap<YearMonth, List<String>>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");
        YearMonth startDate = YearMonth.parse(startDateString, formatter);
        YearMonth endDate = YearMonth.parse(endDateString, formatter);

        while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
            List<String> twoDaysPerMonth = new ArrayList<String>();

            if (isValidMonth(startDate)) {
                twoDaysPerMonth.add(startDate.format(formatter) + "_01_desktop");
                twoDaysPerMonth.add(startDate.format(formatter) + "_15_desktop");
            } else {
                twoDaysPerMonth.add(startDate.format(formatter) + "_01_desktop");
            }

            monthMap.put(startDate, twoDaysPerMonth);
            startDate = startDate.plusMonths(1);
        }
        return monthMap;
    }

    private Map<YearMonth, String> generateQueryForEachMonthInPeriodForCountry(Map<YearMonth, List<String>> monthMap,
            String countryCode) {
        Map<YearMonth, String> queryForEachMonth = new HashMap<YearMonth, String>();

        for (var month : monthMap.entrySet()) {
            List<String> twoDaysInMonth = month.getValue();
            String query;
            if (twoDaysInMonth.size() > 1) {
                String firstTimestamp = twoDaysInMonth.get(0);
                String secondTimestamp = twoDaysInMonth.get(1);

                query = "SELECT DISTINCT * FROM `httparchive.technologies." + firstTimestamp + "` where url like '%."
                        + countryCode
                        + "/' AND info !='' union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                        + secondTimestamp + "` where url like '%." + countryCode + "/' AND info !=''";
            } else {
                String firstTimestamp = twoDaysInMonth.get(0);

                query = "SELECT DISTINCT * FROM `httparchive.technologies." + firstTimestamp + "` where url like '%."
                        + countryCode + "/' AND info !=''";
            }

            queryForEachMonth.put(month.getKey(), query);
        }
        return queryForEachMonth;
    }

    /**
     * For the month 2018_05 and every month after 2018_12 there is only a single
     * technology table in the BigQuery DB. Every Date before that has two. One on
     * the first one on the 15th.
     */
    private boolean isValidMonth(YearMonth currentDate) {
        boolean isValid = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");

        if (currentDate.compareTo(YearMonth.parse("2018_05", formatter)) == 0
                || currentDate.isAfter(YearMonth.parse("2018_12", formatter))) {
            isValid = false;
        }

        return isValid;
    }

}
