package com.example.MasterProjekt.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import com.example.MasterProjekt.util.NvdJsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NvdController {

    @Autowired
    NvdJsonParser nvdJsonParser;

    @GetMapping("/parseNVDToDatabase")
    public void test2() throws InterruptedException {
        File jsonDirectory = new File(
                "D:\\Projekte\\Master-projekt\\MasterProjektBackend\\src\\main\\resources\\nvDJsons");
        File[] jsonDirListing = jsonDirectory.listFiles();
        for (File json : jsonDirListing) {
            nvdJsonParser.parseNvdJson(json.toPath().toString().replace("\\", "\\\\"), jsonDirListing.length);
        }
    }
}
