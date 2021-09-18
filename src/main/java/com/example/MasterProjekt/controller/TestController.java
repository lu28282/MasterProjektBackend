package com.example.MasterProjekt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.MasterProjekt.model.Cpe;
import com.example.MasterProjekt.model.Vulnerability;
import com.example.MasterProjekt.repository.VulnerabilityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * This Contoller is only used to test whether the custom query to retrieve all
 * vulnerabilities refering to a given software works as intended. Additionally
 * it estimated the time safed compared to solving the issue programmatically.
 * This is not to be shipped in a live environment.
 */
@RestController
public class TestController {

    VulnerabilityRepository vulnerabilityRepository;

    @Autowired
    public TestController(VulnerabilityRepository vulnerabilityRepository) {
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    /**
     * Testcases:
     * "application_and_content_networking_software"
     * "connections"
     * "outlook"
     * "sophos_anti-virus"
     * "openssl"
     * "linux_kernel"
     *  */ 
    @GetMapping("/testRepo/{software}")
    public void testIfRepositoryMethodWorksAsIntended(@PathVariable("software") String software) {
        // Repo test start
        // long startTimeRepo = System.currentTimeMillis();
        // List<Vulnerability> vulContainingSoftwareUsingRepo = vulnerabilityRepository.findVulBySoftware(software);
        // long endTimeRepo = System.currentTimeMillis();
        // long totalTimeRepo = endTimeRepo - startTimeRepo;
        // Repo test end

        // Java test start
        long startTimeJava = System.currentTimeMillis();
        List<Vulnerability> allVulnerabilities = vulnerabilityRepository.findAll();
        List<Vulnerability> vulContainingSoftwareUsingJava = new ArrayList<Vulnerability>();

        for (Vulnerability vul : allVulnerabilities) {
            Set<Cpe> cpes = vul.getCpeSet();
            for (Cpe cpe : cpes) {
                if (cpe.getSoftware().equalsIgnoreCase(software)) {
                    vulContainingSoftwareUsingJava.add(vul);
                    break;
                }
            }
        }

        long endTimeJava = System.currentTimeMillis();
        long totalTimeJava = endTimeJava - startTimeJava;
        // Java test end

        // Documentation / Results
        // System.out.println("Given Software is ========== " + software + " ==========");
        // System.out.println("Repo List Size: " + vulContainingSoftwareUsingRepo.size() + " ------ " + "Java List Size: "
        //         + vulContainingSoftwareUsingJava.size());

        // vulContainingSoftwareUsingRepo.removeAll(vulContainingSoftwareUsingJava);
        // System.out.println("This should return an empty array: " + vulContainingSoftwareUsingRepo);

        // System.out.println("A Query takes " + totalTimeRepo + " miliseconds, compared to Java Method taking "
        //         + totalTimeJava + " miliseconds.");
        // System.out.println("Which mean the Java Method takes " + totalTimeJava / totalTimeRepo + " times longer");
    }

}
