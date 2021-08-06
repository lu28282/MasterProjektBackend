package com.example.MasterProjekt.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NvdController {
    

    @GetMapping("/nvdTest")
    public void test(){
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
}
