package com.example.MasterProjekt.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.MasterProjekt.cveDTO.BaseMetricV2;
import com.example.MasterProjekt.cveDTO.BaseMetricV3;
import com.example.MasterProjekt.cveDTO.CpeDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NvdController {

    @GetMapping("/nvdTest")
    public void test() {
        ObjectMapper mapper = new ObjectMapper();
        File file = Paths.get("C:\\Users\\Luca\\Desktop\\nvdcve-1.1-2021.json").toFile();
        try {
            JsonNode root = mapper.readTree(file);
            JsonNode CVE_Items = root.get("CVE_Items");
            JsonNode cve = CVE_Items.get(0);
            JsonNode configurations = cve.get("configurations");
            JsonNode nodes = configurations.get("nodes");
            System.out.println("Wir haben die root");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @GetMapping("/parseNVDToDatabase")
    public void test2() {
        ObjectMapper mapper = new ObjectMapper();
        File file = Paths.get("C:\\Users\\Luca\\Desktop\\nvdcve-1.1-2021.json").toFile();
        try {
            JsonNode root = mapper.readTree(file);
            JsonNode CVE_Items = root.get("CVE_Items");
            Iterator<JsonNode> items = CVE_Items.elements();

            items.forEachRemaining(cve -> filterCVE(cve));

            JsonNode cve = CVE_Items.get(0);
            JsonNode configurations = cve.get("configurations");
            JsonNode nodes = configurations.get("nodes");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void filterCVE(JsonNode cveItems) {
        String id = cveItems.get("cve").get("CVE_data_meta").get("ID").asText();
        JsonNode problemTypeNode = cveItems.get("cve").get("problemtype");
        List<String> problemtypeList = filterCveForProblemType(problemTypeNode);
        List<CpeDTO> cpes = filterCveForCpe23Uri(cveItems.get("configurations"));
        JsonNode impact = cveItems.get("impact");
        BaseMetricV2 baseMetricV2 = null;
        BaseMetricV3 baseMetricV3 = null;
        if (impact != null) {
            baseMetricV2 = filterCveForBaseMetricV2(impact);
            baseMetricV3 = filterCveForBaseMetricV3(impact);
        }
        String publishedDate = cveItems.get("publishedDate").asText();
        String lastModifiedDate = cveItems.get("lastModifiedDate").asText();

        System.out.println("===");
        System.out.println(id);
        problemtypeList.forEach(x -> System.out.println(x));
        cpes.forEach(x -> System.out.println(x.getCpe23Uri() + " startdate: " +
        x.getVersionStartIncluding()
        + " enddate: " + x.getVersionEndIncluding()));
        System.out.println("V2 impactScore: " + baseMetricV2.getImpactScore() + " V2 ExploitScore: "
                + baseMetricV2.getExploitabilityScore());
        System.out.println("V3 impactScore: " + baseMetricV3.getImpactScore() + " V3 ExploitScore: "
                + baseMetricV3.getExploitabilityScore());
        System.out.println("publishedDate: " + publishedDate);
        System.out.println("lastModifiedDate: " + lastModifiedDate);
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

    private List<CpeDTO> filterCveForCpe23Uri(JsonNode configurationsNode) {
        List<CpeDTO> cpeList = new ArrayList<CpeDTO>();
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
                            CpeDTO cpeDTO = new CpeDTO();
                            cpeDTO.setCpe23Uri(cpe23Uri);
                            JsonNode versionEndIncludingNode = cpeMatch.get("versionEndIncluding");
                            JsonNode versionStartIncludingNode = cpeMatch.get("versionStartIncluding");
                            if (versionEndIncludingNode != null) {
                                cpeDTO.setVersionEndIncluding(versionEndIncludingNode.asText());
                            }
                            if (versionStartIncludingNode != null) {
                                cpeDTO.setVersionStartIncluding(versionStartIncludingNode.asText());
                            }
                            cpeList.add(cpeDTO);
                        }
                    }
                }
            }
        }
        if (cpeList.size() < 1) {

        }
        return cpeList;
    }

    private BaseMetricV2 filterCveForBaseMetricV2(JsonNode impactNode) {
        BaseMetricV2 baseMetricV2 = new BaseMetricV2();
        try {
            JsonNode baseMetricV2Node = impactNode.get("baseMetricV2");
            Double impactScore = baseMetricV2Node.get("impactScore").asDouble();
            baseMetricV2.setImpactScore(impactScore);
        } catch (NullPointerException ex) {
            System.out.println("No Impact Score found");
        }

        try {
            JsonNode baseMetricV2Node = impactNode.get("baseMetricV2");
            Double exploitabilityScore = baseMetricV2Node.get("exploitabilityScore").asDouble();
            baseMetricV2.setExploitabilityScore(exploitabilityScore);
        } catch (NullPointerException ex) {
            System.out.println("No Exploitation Score found");
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
            System.out.println("No Impact Score found");
        }

        try {
            JsonNode baseMetricV3Node = impactNode.get("baseMetricV3");
            Double exploitabilityScore = baseMetricV3Node.get("exploitabilityScore").asDouble();
            baseMetricV3.setExploitabilityScore(exploitabilityScore);
        } catch (NullPointerException ex) {
            System.out.println("No Exploitation Score found");
        }
        return baseMetricV3;
    }

}
