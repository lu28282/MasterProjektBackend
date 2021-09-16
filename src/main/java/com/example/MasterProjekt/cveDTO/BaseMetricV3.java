package com.example.MasterProjekt.cveDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseMetricV3 {
    
    private double exploitabilityScore;

    private double impactScore;
}
