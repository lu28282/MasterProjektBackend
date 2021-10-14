package com.example.MasterProjekt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.example.MasterProjekt.util.QueryBuilder;

import org.junit.jupiter.api.Test;

public class QueryBuilderTest {

        @Test
        public void buildCorrectQueryOverMultipleMonth() {
                QueryBuilder queryBuilder = new QueryBuilder();
                Map<YearMonth, String> resultQueryForEachMonth = new HashMap<YearMonth, String>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");

                resultQueryForEachMonth.put(YearMonth.parse("2016_10", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2016_10_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2016_10_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                resultQueryForEachMonth.put(YearMonth.parse("2016_11", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2016_11_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2016_11_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                resultQueryForEachMonth.put(YearMonth.parse("2016_12", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2016_12_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2016_12_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                resultQueryForEachMonth.put(YearMonth.parse("2017_01", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2017_01_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2017_01_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                resultQueryForEachMonth.put(YearMonth.parse("2017_02", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2017_02_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2017_02_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                resultQueryForEachMonth.put(YearMonth.parse("2017_03", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2017_03_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')"
                                                + " union DISTINCT SELECT DISTINCT * FROM `httparchive.technologies."
                                                + "2017_03_15_desktop" + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                assertTrue(resultQueryForEachMonth.equals(
                                queryBuilder.getTechnologiesInPeriodForCountryQuery("2016_10", "2017_03", "de")));
        }

        @Test
        public void buildCorrectQueryASingleMonth() {
                QueryBuilder queryBuilder = new QueryBuilder();
                Map<YearMonth, String> resultQueryForEachMonth = new HashMap<YearMonth, String>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM");

                resultQueryForEachMonth.put(YearMonth.parse("2020_08", formatter),
                                "SELECT DISTINCT * FROM `httparchive.technologies." + "2020_08_01_desktop"
                                                + "` where url like '%." + "de"
                                                + "/' AND info !='' AND regexp_contains(info, '([0-9]+[.]*)+') AND NOT regexp_contains(info, '[a-zA-Z][a-zA-Z]+')");
                assertTrue(resultQueryForEachMonth.equals(
                                queryBuilder.getTechnologiesInPeriodForCountryQuery("2020_08", "2020_08", "de")));
        }

        @Test
        public void buildNoQueryWithWrongOrderOfMonth() {
                QueryBuilder queryBuilder = new QueryBuilder();
                Map<YearMonth, String> emptyResultMap = new HashMap<YearMonth, String>();
                assertTrue(emptyResultMap.equals(
                                queryBuilder.getTechnologiesInPeriodForCountryQuery("2020_10", "2020_08", "de")));
        }
}
