package com.example.MasterProjekt.cveDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseMetricV2 {
    
    private double exploitabilityScore;

    private double impactScore;
}
