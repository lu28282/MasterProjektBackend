package com.example.MasterProjekt.controller;

import java.io.File;
import java.io.FileNotFoundException;

import com.example.MasterProjekt.util.NvdJsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class NvdController {

    @Autowired
    NvdJsonParser nvdJsonParser;

    @GetMapping("/parseNVDToDatabase")
    public void parseNVDToDatabase() throws InterruptedException, FileNotFoundException {
        File jsonDirectory = ResourceUtils.getFile("classpath:nvdJsons");
        File[] jsonDirListing = jsonDirectory.listFiles();
        for (File json : jsonDirListing) {
            nvdJsonParser.parseNvdJson(json.toPath().toString().replace("\\", "\\\\"), jsonDirListing.length);
        }
    }
}
