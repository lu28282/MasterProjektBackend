package com.example.MasterProjekt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.example.MasterProjekt.dao.VulnerabilityRepository;
import com.example.MasterProjekt.model.BaseMetricV2;
import com.example.MasterProjekt.model.BaseMetricV3;
import com.example.MasterProjekt.model.Cpe;
import com.example.MasterProjekt.model.Vulnerability;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NvdJsonParser {

    private VulnerabilityRepository vulnerabilityRepository;

    @Autowired
    public NvdJsonParser(VulnerabilityRepository vulnerabilityRepository) {
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    public void parseNvdJson(String pathToJson) {
        ObjectMapper mapper = new ObjectMapper();
        File file = Paths.get(pathToJson).toFile();
        try {
            JsonNode root = mapper.readTree(file);
            JsonNode CVE_Items = root.get("CVE_Items");
            Iterator<JsonNode> items = CVE_Items.elements();

            items.forEachRemaining(cve -> filterCVE(cve));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean cveToVulnerabilityToDatabase(String id, List<String> problemtypeList, Set<Cpe> cpeSet,
            BaseMetricV2 baseMetricV2, BaseMetricV3 baseMetricV3, String publishedDate, String lastModifiedDate) {
        boolean addedVulnerabilitySuccesfully = true;
        Vulnerability vulnerability;
        publishedDate = publishedDate.substring(0, 16);
        lastModifiedDate = lastModifiedDate.substring(0, 16);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date pDate = null;
        Date lmDate = null;
        try {
            pDate = format.parse(publishedDate);
            lmDate = format.parse(lastModifiedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (id.equals("") || problemtypeList.isEmpty() || cpeSet.isEmpty()) {
            addedVulnerabilitySuccesfully = false;
        } else {
            vulnerability = new Vulnerability(id, problemtypeList, cpeSet);
            vulnerability.setBaseMetricV2(baseMetricV2);
            vulnerability.setBaseMetricV3(baseMetricV3);
            vulnerability.setPublishedDate(pDate);
            vulnerability.setLastModifiedDate(lmDate);
            vulnerabilityRepository.save(vulnerability);
        }

        return addedVulnerabilitySuccesfully;
    }

    private void filterCVE(JsonNode cveItems) {
        String id = cveItems.get("cve").get("CVE_data_meta").get("ID").asText();
        JsonNode problemTypeNode = cveItems.get("cve").get("problemtype");
        List<String> problemtypeList = filterCveForProblemType(problemTypeNode);
        Set<Cpe> cpeSet = filterCveForCpe23Uri(cveItems.get("configurations"));
        JsonNode impact = cveItems.get("impact");
        BaseMetricV2 baseMetricV2 = null;
        BaseMetricV3 baseMetricV3 = null;
        if (impact != null) {
            baseMetricV2 = filterCveForBaseMetricV2(impact);
            baseMetricV3 = filterCveForBaseMetricV3(impact);
        }
        String publishedDate = cveItems.get("publishedDate").asText();
        String lastModifiedDate = cveItems.get("lastModifiedDate").asText();

        cveToVulnerabilityToDatabase(id, problemtypeList, cpeSet, baseMetricV2, baseMetricV3, publishedDate,
                lastModifiedDate);
    }

    private List<String> filterCveForProblemType(JsonNode problemTypeNode) {
        List<String> problemtypeList = new ArrayList<String>();

        Iterator<JsonNode> problemTypeIterator = problemTypeNode.iterator();
        while (problemTypeIterator.hasNext()) {
            Iterator<JsonNode> problemTypeDataIterator = problemTypeIterator.next().iterator();
            while (problemTypeDataIterator.hasNext()) {
                Iterator<JsonNode> descriptionIterator = problemTypeDataIterator.next().iterator();
                while (descriptionIterator.hasNext()) {
                    Iterator<JsonNode> descriptionContentIterator = descriptionIterator.next().iterator();
                    while (descriptionContentIterator.hasNext()) {
                        JsonNode descriptionContentNode = descriptionContentIterator.next();
                        problemtypeList.add(descriptionContentNode.get("value").asText());
                    }
                }
            }
        }

        return problemtypeList;
    }

    private Set<Cpe> filterCveForCpe23Uri(JsonNode configurationsNode) {
        Set<Cpe> cpeSet = new HashSet<Cpe>();
        JsonNode nodes = configurationsNode.get("nodes");
        Iterator<JsonNode> nodesIterator = nodes.iterator();
        while (nodesIterator.hasNext()) {
            JsonNode node = nodesIterator.next();
            String operator = node.get("operator").asText();
            if (operator.equals("OR")) {
                Iterator<JsonNode> cpeMatchIterator = node.get("cpe_match").iterator();
                while (cpeMatchIterator.hasNext()) {
                    JsonNode cpeMatch = cpeMatchIterator.next();
                    Boolean vulnerable = cpeMatch.get("vulnerable").asBoolean();
                    if (vulnerable) {
                        String cpe23Uri = cpeMatch.get("cpe23Uri").asText();
                        if (cpe23Uri.contains(":a:")) {
                            Cpe cpe = subStringCpe23UriAndAddToCpe(cpe23Uri);
                            cpe.setCpe23Uri(cpe23Uri);
                            JsonNode versionStartIncludingNode = cpeMatch.get("versionStartIncluding");
                            JsonNode versionStartExcludingNode = cpeMatch.get("versionStartExcluding");
                            JsonNode versionEndIncludingNode = cpeMatch.get("versionEndIncluding");
                            JsonNode versionEndExcludingNode = cpeMatch.get("versionEndExcluding");
                            if (versionStartIncludingNode != null) {
                                cpe.setVersionStartIncluding(versionStartIncludingNode.asText());
                            } else if (versionStartExcludingNode != null) {
                                cpe.setVersionStartExcluding(versionStartExcludingNode.asText());
                            }
                            if (versionEndIncludingNode != null) {
                                cpe.setVersionEndIncluding(versionEndIncludingNode.asText());
                            } else if (versionEndExcludingNode != null) {
                                cpe.setVersionEndExcluding(versionEndExcludingNode.asText());
                            }
                            cpeSet.add(cpe);
                        }
                    }
                }
            }
        }
        return cpeSet;
    }

    private Cpe subStringCpe23UriAndAddToCpe(String cpe23Uri) {
        Cpe cpe = new Cpe();
        cpe.setCpe23Uri(cpe23Uri);
        String[] splitUri = cpe23Uri.split(":");
        String software = splitUri[4];
        String version = splitUri[5];

        cpe.setSoftware(software);
        if (!version.equals("*")) {
            cpe.setVersion(version);
        }

        return cpe;
    }

    private BaseMetricV2 filterCveForBaseMetricV2(JsonNode impactNode) {
        BaseMetricV2 baseMetricV2 = new BaseMetricV2();
        try {
            JsonNode baseMetricV2Node = impactNode.get("baseMetricV2");
            Double impactScore = baseMetricV2Node.get("impactScore").asDouble();
            baseMetricV2.setImpactScore(impactScore);
        } catch (NullPointerException ex) {
        }

        try {
            JsonNode baseMetricV2Node = impactNode.get("baseMetricV2");
            Double exploitabilityScore = baseMetricV2Node.get("exploitabilityScore").asDouble();
            baseMetricV2.setExploitabilityScore(exploitabilityScore);
        } catch (NullPointerException ex) {
        }
        return baseMetricV2;
    }

    private BaseMetricV3 filterCveForBaseMetricV3(JsonNode impactNode) {
        BaseMetricV3 baseMetricV3 = new BaseMetricV3();
        try {
            JsonNode baseMetricV3Node = impactNode.get("baseMetricV3");
            Double impactScore = baseMetricV3Node.get("impactScore").asDouble();
            baseMetricV3.setImpactScore(impactScore);
        } catch (NullPointerException ex) {
        }

        try {
            JsonNode baseMetricV3Node = impactNode.get("baseMetricV3");
            Double exploitabilityScore = baseMetricV3Node.get("exploitabilityScore").asDouble();
            baseMetricV3.setExploitabilityScore(exploitabilityScore);
        } catch (NullPointerException ex) {
        }
        return baseMetricV3;
    }
}