package com.example.MasterProjekt.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.example.MasterProjekt.util.NvdJsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NvdController {

    @Autowired
    NvdJsonParser nvdJsonParser;

    @GetMapping("/parseNVDToDatabase")
    public void test2() {
        File jsonDirectory = new File(
                "D:\\Projekte\\Master-projekt\\MasterProjektBackend\\src\\main\\resources\\nvDJsons");
        File[] jsonDirListing = jsonDirectory.listFiles();
        int amountOfFiles = 1;
        System.out.println("Start parsing");
        for (File json : jsonDirListing) {
            nvdJsonParser.parseNvdJson(json.toPath().toString().replace("\\", "\\\\"));
            System.out.println("File " + amountOfFiles + " complete of " + jsonDirListing.length  + " total files" );
            amountOfFiles++;
        }
        System.out.println("Done");

        // nvdJsonParser.parseNvdJson("D:\\Projekte\\Master-projekt\\MasterProjektBackend\\src\\main\\resources\\nvDJsons\\nvdcve-1.1-2003.json");
    }
}
